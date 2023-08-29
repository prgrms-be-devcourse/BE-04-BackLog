package dev.backlog.domain.post.api;

import com.epages.restdocs.apispec.Schema;
import dev.backlog.common.config.ControllerTestConfig;
import dev.backlog.common.util.TestFixtureUtil;
import dev.backlog.domain.comment.model.Comment;
import dev.backlog.domain.post.dto.PostCreateRequest;
import dev.backlog.domain.post.dto.PostResponse;
import dev.backlog.domain.post.dto.PostSliceResponse;
import dev.backlog.domain.post.dto.PostSummaryResponse;
import dev.backlog.domain.post.dto.PostUpdateRequest;
import dev.backlog.domain.post.model.Post;
import dev.backlog.domain.post.service.PostService;
import dev.backlog.domain.series.model.Series;
import dev.backlog.domain.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.List;
import java.util.Set;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest extends ControllerTestConfig {

    @MockBean
    private PostService postService;

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

        User user = TestFixtureUtil.createUser();
        ReflectionTestUtils.setField(user, "id", userId);

        Series series = TestFixtureUtil.createSeries(user);
        ReflectionTestUtils.setField(series, "id", seriesId);

        Post post = TestFixtureUtil.createPost(user, series);
        ReflectionTestUtils.setField(post, "id", postId);

        Comment comment1 = TestFixtureUtil.createComment(user, post);
        ReflectionTestUtils.setField(comment1, "id", comment1Id);

        Comment comment2 = TestFixtureUtil.createComment(user, post);
        ReflectionTestUtils.setField(comment2, "id", comment2Id);

        PostResponse postResponse = PostResponse.from(post, List.of(comment1, comment2));
        when(postService.findPostById(postId, userId)).thenReturn(postResponse);

        //when, then
        mockMvc.perform(get("/api/posts/{postId}", postId)
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(postId))
                .andExpect(jsonPath("$.series").isNotEmpty())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.title").value("test"))
                .andExpect(jsonPath("$.viewCount").value(0))
                .andExpect(jsonPath("$.content").value("test"))
                .andExpect(jsonPath("$.summary").value("test"))
                .andExpect(jsonPath("$.isPublic").value(true))
                .andExpect(jsonPath("$.path").value("test"))
                .andExpect(jsonPath("$.createdAt").isEmpty())
                .andExpect(jsonPath("$.comments", hasSize(2)))
                .andDo(MockMvcResultHandlers.print());
    }

    @DisplayName("사용자가 좋아요를 누른 게시글 목록을 최신 순서로 반환한다.")
    @Test
    void findLikedPosts() throws Exception {
        //given
        Long userId = 1L;
        User user = TestFixtureUtil.createUser();

        int postCount = 10;
        List<Post> posts = TestFixtureUtil.createPosts(user, null, postCount);

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

    @DisplayName("사용자의 게시물 업데이트 요청을 받아 업데이트 한 뒤 204 상태코드를 반환한다.")
    @Test
    void updatePostTest() throws Exception {
        Long postId = 1L;
        PostUpdateRequest request = getPostUpdateRequest();
        System.out.println("asdasdasdasd" + objectMapper.writeValueAsString(request));
        doNothing().when(postService).updatePost(any(PostUpdateRequest.class), anyLong(), anyLong());

        mockMvc.perform(put("/api/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent())
                .andDo(document("post-update",
                                resourceDetails().tag("게시물").description("게시물 업데이트")
                                        .requestSchema(Schema.schema("PostUpdateRequest")),
                                pathParameters(parameterWithName("postId").description("게시물 식별자")),
                                requestFields(
                                        fieldWithPath("series").type(JsonFieldType.STRING).description("시리즈"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                        fieldWithPath("hashtags").type(JsonFieldType.ARRAY).description("해시태그"),
                                        fieldWithPath("summary").type(JsonFieldType.STRING).description("요약"),
                                        fieldWithPath("isPublic").type(JsonFieldType.BOOLEAN).description("공개 여부"),
                                        fieldWithPath("thumbnailImage").type(JsonFieldType.STRING).description("썸네일 URL"),
                                        fieldWithPath("path").type(JsonFieldType.STRING).description("게시물 경로"))
                        )
                );
    }

    @DisplayName("사용자와 시리즈 이름으로 게시글 목록을 과거순으로 반환한다.")
    @Test
    void findSeriesPosts() throws Exception {
        //given
        Long userId = 1L;
        User user = TestFixtureUtil.createUser();

        Series series = TestFixtureUtil.createSeries(user);

        int postCount = 10;
        List<Post> posts = TestFixtureUtil.createPosts(user, series, postCount);

        int page = 1;
        int size = 20;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.ASC, "createdAt");

        int likeCount = 0;
        int commentCount = 0;
        Slice<Post> slice = new SliceImpl<>(posts, pageRequest, false);
        Slice<PostSummaryResponse> postSummaryResponses = slice
                .map(post -> PostSummaryResponse.of(post, commentCount, likeCount));

        PostSliceResponse<PostSummaryResponse> postSliceResponse = PostSliceResponse.from(postSummaryResponses);
        when(postService.findPostsByUserAndSeries(userId, series.getName(), pageRequest)).thenReturn(postSliceResponse);

        //when, then
        mockMvc.perform(get("/api/posts")
                        .param("series", series.getName())
                        .param("userId", String.valueOf(userId))
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .param("sort", "createdAt,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").value(postCount))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.data", hasSize(postCount)))
                .andDo(MockMvcResultHandlers.print());
    }

    private PostUpdateRequest getPostUpdateRequest() {
        return new PostUpdateRequest(
                "시리즈",
                "변경된 제목",
                "변경된 내용",
                Set.of("변경된 해쉬태그"),
                "변경된 요약",
                false,
                "변경된 URL",
                "변경된 경로"
        );
    }

}
