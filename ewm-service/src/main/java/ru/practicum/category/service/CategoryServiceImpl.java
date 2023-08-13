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

import static ru.practicum.util.Validation.validateStringField;

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
        try {
            category = repository.add(category);
        } catch (RuntimeException e) {
            reportNameUniqueConflict(e, category);
        }
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto patch(long catId, NewCategoryDto newCategoryDto) {
        Category category = repository.findById(catId);
        String newName = newCategoryDto.getName();
        validateStringField(newName, "название категории", 1, 50);
        category.setName(newCategoryDto.getName());
        try {
            category = repository.update(category);
        } catch (RuntimeException e) {
            reportNameUniqueConflict(e, category);
        }
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

    private void reportNameUniqueConflict(RuntimeException e, Category category) {
        String error = e.getMessage();
        String constraint = "uq_category_name";
        if (error.contains(constraint)) {
            error = String.format("Категория с названием %s уже существует", category.getName());
            log.warn("Попытка дублирования имени категории: {}", category.getName());
            throw new ConflictException(error);
        }
        throw new RuntimeException("Ошибка при передаче данных в БД");
    }
}