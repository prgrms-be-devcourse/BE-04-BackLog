package dev.backlog.domain.post.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.backlog.common.util.TestStubUtil;
import dev.backlog.domain.comment.model.Comment;
import dev.backlog.domain.post.dto.PostCreateRequest;
import dev.backlog.domain.post.dto.PostResponse;
import dev.backlog.domain.post.dto.PostSliceResponse;
import dev.backlog.domain.post.dto.PostSummaryResponse;
import dev.backlog.domain.post.model.Post;
import dev.backlog.domain.post.service.PostService;
import dev.backlog.domain.series.model.Series;
import dev.backlog.domain.user.model.Email;
import dev.backlog.domain.user.model.OAuthProvider;
import dev.backlog.domain.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @DisplayName("게시물 생성 요청을 받아 처리 후 201 코드를 반환하고 게시물 조회 URI를 반환한다.")
    @Test
    void testCreate() throws Exception {
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

    @DisplayName("게시글을 상세 조회할 수 있다.")
    @Test
    void findPost() throws Exception {
        //given
        Long userId = 1L;
        Long postId = 1L;
        Long comment1Id = 1L;
        Long comment2Id = 2L;
        Long seriesId = 1L;

        User user = User.builder()
                .oauthProvider(OAuthProvider.KAKAO)
                .oauthProviderId(String.valueOf(UUID.randomUUID()))
                .nickname("test")
                .email(new Email("test"))
                .profileImage("test")
                .introduction("test")
                .blogTitle("test")
                .build();
        ReflectionTestUtils.setField(user, "id", userId);

        Series series = Series.builder()
                .user(user)
                .name("test")
                .build();
        ReflectionTestUtils.setField(series, "id", seriesId);

        Post post = Post.builder()
                .series(series)
                .user(user)
                .title("test")
                .content("test")
                .summary("test")
                .isPublic(true)
                .thumbnailImage("test")
                .path("test")
                .build();
        ReflectionTestUtils.setField(post, "id", postId);

        Comment comment1 = Comment.builder()
                .writer(user)
                .post(post)
                .content("test1")
                .isDeleted(false)
                .build();
        ReflectionTestUtils.setField(comment1, "id", comment1Id);

        Comment comment2 = Comment.builder()
                .writer(user)
                .post(post)
                .content("test2")
                .isDeleted(false)
                .build();
        ReflectionTestUtils.setField(comment2, "id", comment2Id);
        PostResponse postResponse = PostResponse.from(post, List.of(comment1, comment2));
        when(postService.findPostById(postId, userId)).thenReturn(postResponse);

        //when, then
        mockMvc.perform(get("/api/posts/{postId}", postId)
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("사용자가 좋아요를 누른 게시글 목록을 최신 순서로 반환한다.")
    @Test
    void findLikedPosts() throws Exception {
        //given
        Long userId = 1L;
        User user = TestStubUtil.createUser();

        int postCount = 10;
        List<Post> posts = TestStubUtil.createPosts(user, postCount);

        int page = 1;
        int size = 20;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        int likeCount = 0;
        int commentCount = 0;
        Slice<Post> slice = new SliceImpl<>(posts, pageRequest, false);
        Slice<PostSummaryResponse> postSummaryResponses = slice
                .map(post -> PostSummaryResponse.of(post, commentCount, likeCount));

        PostSliceResponse<PostSummaryResponse> postSliceResponse = PostSliceResponse.from(postSummaryResponses);
        when(postService.findLikedPostsByUser(userId, pageRequest)).thenReturn(postSliceResponse);

        //when, then
        mockMvc.perform(get("/api/posts/like")
                        .param("userId", String.valueOf(userId))
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").value(postCount))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.data", hasSize(postCount)))
                .andDo(MockMvcResultHandlers.print());
    }

}
