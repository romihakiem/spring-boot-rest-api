package com.skeleton.api.service.impl;

import com.skeleton.api.dto.request.ItemRequest;
import com.skeleton.api.dto.response.ItemResponse;
import com.skeleton.api.dto.response.PageResponse;
import com.skeleton.api.entity.Item;
import com.skeleton.api.entity.ItemStatus;
import com.skeleton.api.entity.Role;
import com.skeleton.api.entity.User;
import com.skeleton.api.exception.ResourceNotFoundException;
import com.skeleton.api.exception.UnauthorizedException;
import com.skeleton.api.repository.ItemRepository;
import com.skeleton.api.repository.UserRepository;
import com.skeleton.api.service.ItemService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemResponse create(ItemRequest request, String ownerEmail) {
        User owner = getUserByEmail(ownerEmail);

        Item item = Item.builder()
                .name(request.getName())
                .description(request.getDescription() != null ? request.getDescription() : "")
                .category(request.getCategory() != null ? request.getCategory() : "Umum")
                .price(request.getPrice() != null ? request.getPrice() : java.math.BigDecimal.ZERO)
                .stock(request.getStock() != null ? request.getStock() : 0)
                .status(request.getStatus() != null ? request.getStatus() : ItemStatus.ACTIVE)
                .owner(owner)
                .build();

        Item saved = itemRepository.save(item);
        return ItemResponse.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ItemResponse> getAll(Pageable pageable, String search, String category) {
        Specification<Item> spec = buildSpecification(search, category);
        Page<ItemResponse> page = itemRepository.findAll(spec, pageable).map(ItemResponse::fromEntity);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResponse getById(Long id) {
        return ItemResponse.fromEntity(getItemOrThrow(id));
    }

    @Override
    @Transactional
    public ItemResponse update(Long id, ItemRequest request, String requesterEmail) {
        Item item = getItemOrThrow(id);
        assertOwnerOrAdmin(item, requesterEmail);

        if (request.getName() != null)
            item.setName(request.getName());
        if (request.getDescription() != null)
            item.setDescription(request.getDescription());
        if (request.getCategory() != null)
            item.setCategory(request.getCategory());
        if (request.getPrice() != null)
            item.setPrice(request.getPrice());
        if (request.getStock() != null)
            item.setStock(request.getStock());
        if (request.getStatus() != null)
            item.setStatus(request.getStatus());

        Item saved = itemRepository.save(item);
        return ItemResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public void delete(Long id, String requesterEmail) {
        Item item = getItemOrThrow(id);
        assertOwnerOrAdmin(item, requesterEmail);
        itemRepository.delete(item);
    }

    // ---------- helpers ----------

    private Item getItemOrThrow(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void assertOwnerOrAdmin(Item item, String requesterEmail) {
        User requester = getUserByEmail(requesterEmail);
        boolean isOwner = item.getOwner().getId().equals(requester.getId());
        boolean isAdmin = requester.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException("You are not allowed to modify this item");
        }
    }

    private Specification<Item> buildSpecification(String search, String category) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                String like = "%" + search.toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("name")), like));
            }

            if (category != null && !category.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("category")), category.toLowerCase()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
