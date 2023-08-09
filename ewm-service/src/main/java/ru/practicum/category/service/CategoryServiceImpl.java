package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.Category;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.error.exceptions.ConflictException;
import ru.practicum.event.repository.EventRepository;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;
    private final EventRepository eventRepo;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAll(int from, int size) {
        int page = from / size;
        from = page * size;
        List<Category> categories = repository.getAll(from, size);
        return CategoryMapper.toCategoryDto(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto findById(long catId) {
        Category category = repository.findById(catId);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto add(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.toCategory(newCategoryDto);
        category = repository.add(category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto patch(long catId, NewCategoryDto newCategoryDto) {
        Category category = repository.findById(catId);
        category.setName(newCategoryDto.getName());
        category = repository.update(category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public void delete(long catId) {
        //с категорией не должно быть связано ни одного события.
        long eventsQty = eventRepo.countEventsByCategoryId(catId);
        if (eventsQty == 0) {
            repository.delete(catId);
        } else {
            throw new ConflictException(
                    String.format("С категорией с id %d связано %d событий", catId, eventsQty));
        }
    }
}