package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Review review;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
        objectMapper = new ObjectMapper();

        review = Review.builder()
                .reviewId(1L)
                .content("content")
                .isPositive(true)
                .userId(1L)
                .filmId(1L)
                .build();
    }

    @Test
    void create() throws Exception {
        when(reviewService.create(any())).thenReturn(review);

        mockMvc.perform(post("/reviews/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isCreated());

        verify(reviewService, times(1)).create(any());
    }

    @Test
    void update() throws Exception {
        when(reviewService.update(any())).thenReturn(review);

        mockMvc.perform(put("/reviews/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(review)))
                .andExpect(status().isOk());

        verify(reviewService, times(1)).update(any());
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/reviews/1"))
                .andExpect(status().isOk());

        verify(reviewService, times(1)).delete(1L);
    }

    @Test
    void findById() throws Exception {
        when(reviewService.findById(1L)).thenReturn(review);

        mockMvc.perform(get("/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").value(1));

        verify(reviewService, times(1)).findById(1L);
    }

    @Test
    void findCountReviewsByFilmId() throws Exception {
        when(reviewService.findCountReviewsByFilmId(1L, 5)).thenReturn(List.of(review));

        mockMvc.perform(get("/reviews?filmId=1&count=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reviewId").value(1));

        verify(reviewService, times(1)).findCountReviewsByFilmId(1L, 5);
    }

    @Test
    void likeReview() throws Exception {
        when(reviewService.likeReview(1L, 1L)).thenReturn(review);

        mockMvc.perform(put("/reviews/1/like/1"))
                .andExpect(status().isOk());

        verify(reviewService, times(1)).likeReview(1L, 1L);
    }

    @Test
    void dislikeReview() throws Exception {
        when(reviewService.dislikeReview(1L, 1L)).thenReturn(review);

        mockMvc.perform(put("/reviews/1/dislike/1"))
                .andExpect(status().isOk());

        verify(reviewService, times(1)).dislikeReview(1L, 1L);
    }

    @Test
    void removeLike() throws Exception {
        when(reviewService.removeLike(1L, 1L)).thenReturn(review);

        mockMvc.perform(MockMvcRequestBuilders.delete("/reviews/1/like/1"))
                .andExpect(status().isOk());

        verify(reviewService, times(1)).removeLike(1L, 1L);
    }

    @Test
    void removeDislike() throws Exception {
        when(reviewService.removeDislike(1L, 1L)).thenReturn(review);

        mockMvc.perform(MockMvcRequestBuilders.delete("/reviews/1/dislike/1"))
                .andExpect(status().isOk());

        verify(reviewService, times(1)).removeDislike(1L, 1L);
    }
}
