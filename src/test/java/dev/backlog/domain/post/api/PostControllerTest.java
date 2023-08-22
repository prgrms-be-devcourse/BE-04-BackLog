package dev.backlog.domain.post.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.backlog.domain.post.dto.PostCreateRequest;
import dev.backlog.domain.post.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testCreate() throws Exception {
        Long userId = 1L;
        Long postId = 2L;
        PostCreateRequest request = new PostCreateRequest(
                "series",
                "제목", "내용",
                null,
                "요약",
                true,
                "썸네일",
                "경로"
        );
        when(postService.create(any(PostCreateRequest.class), eq(userId)))
                .thenReturn(postId);

        mockMvc.perform(post("/api/posts")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/posts/" + postId));
    }

}
