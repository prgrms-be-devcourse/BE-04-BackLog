package dev.backlog.domain.post.api;

import com.epages.restdocs.apispec.Schema;
import dev.backlog.common.config.ControllerTestConfig;
import dev.backlog.domain.comment.dto.CommentResponse;
import dev.backlog.domain.post.dto.PostCreateRequest;
import dev.backlog.domain.post.dto.PostResponse;
import dev.backlog.domain.post.dto.PostSliceResponse;
import dev.backlog.domain.post.dto.PostSummaryResponse;
import dev.backlog.domain.post.dto.PostUpdateRequest;
import dev.backlog.domain.post.service.PostService;
import dev.backlog.domain.series.dto.SeriesResponse;
import dev.backlog.domain.user.dto.Writer;
import dev.backlog.domain.series.dto.SeriesResponse;
import dev.backlog.domain.user.dto.Writer;
import dev.backlog.domain.series.model.Series;
import dev.backlog.domain.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;
import java.util.Set;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
        final long postId = 1l;
        final long seriesId = 1l;
        final long userId = 1l;
        final long commentId = 1l;
        PostResponse postResponse = getPostResponse(postId, seriesId, userId, commentId);
        when(postService.findPostById(any(), any())).thenReturn(postResponse);

        //when, then
        mockMvc.perform(get("/api/posts/{postId}", postId)
                        .header("AuthorizationCode", "tmp"))
                .andExpect(status().isOk())
                .andDo(document("post-find",
                                resourceDetails().tag("게시물").description("게시물 상세 조회")
                                        .responseSchema(Schema.schema("PostResponse")),
                                pathParameters(parameterWithName("postId").description("게시물 식별자")),
                                responseFields(
                                        fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 번호"),
                                        fieldWithPath("series").type(JsonFieldType.OBJECT).description("시리즈"),
                                        fieldWithPath("series.seriesId").type(JsonFieldType.NUMBER).description("시리즈 번호"),
                                        fieldWithPath("series.seriesName").type(JsonFieldType.STRING).description("시리즈 이름"),
                                        fieldWithPath("userId").type(JsonFieldType.NUMBER).description("게시글 작성자 번호"),
                                        fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                                        fieldWithPath("viewCount").type(JsonFieldType.NUMBER).description("게시글 조회수"),
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 본문"),
                                        fieldWithPath("summary").type(JsonFieldType.STRING).description("게시글 요약"),
                                        fieldWithPath("isPublic").type(JsonFieldType.BOOLEAN).description("게시글 공개 여부"),
                                        fieldWithPath("path").type(JsonFieldType.STRING).description("게시글 저장 경로"),
                                        fieldWithPath("createdAt").type(JsonFieldType.NULL).description("게시글 작성 시간"),
                                        fieldWithPath("comments[]").type(JsonFieldType.ARRAY).description("댓글"),
                                        fieldWithPath("comments[].commentId").type(JsonFieldType.NUMBER).description("댓글 작성자 번호"),
                                        fieldWithPath("comments[].writer").type(JsonFieldType.OBJECT).description("댓글 작성자"),
                                        fieldWithPath("comments[].writer.userId").type(JsonFieldType.NUMBER).description("댓글 작성자 번호"),
                                        fieldWithPath("comments[].writer.nickname").type(JsonFieldType.STRING).description("댓글 작성자 닉네임"),
                                        fieldWithPath("comments[].content").type(JsonFieldType.STRING).description("댓글 본문"),
                                        fieldWithPath("comments[].createdAt").type(JsonFieldType.NULL).description("댓글 작성 시간")

                                )
                        )
                );
    }

    @DisplayName("사용자가 좋아요를 누른 게시글 목록을 최신 순서로 반환한다.")
    @Test
    void findLikedPosts() throws Exception {
        //given
        final long postId = 1l;
        final long userId = 1l;
        PostSliceResponse<PostSummaryResponse> postSliceResponse = getPostSliceResponse(postId, userId);
        when(postService.findLikedPostsByUser(any(), any(PageRequest.class))).thenReturn(postSliceResponse);

        //when, then
        mockMvc.perform(get("/api/posts/like")
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(20))
                        .param("sort", "createdAt,asc")
                        .header("AuthorizationCode", "tmp"))
                .andExpect(status().isOk())
                .andDo(document("posts-find-like",
                                resourceDetails().tag("게시물").description("좋아요 누른 게시물 조회")
                                        .responseSchema(Schema.schema("PostSliceResponse")),
                                queryParameters(
                                        parameterWithName("page").description("현재 페이지"),
                                        parameterWithName("size").description("페이지 당 게시물 수"),
                                        parameterWithName("sort").description("정렬 기준")
                                ),
                                responseFields(
                                        fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("게시글 수"),
                                        fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN).description("마지막 페이지 체크"),
                                        fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("게시글 데이터"),
                                        fieldWithPath("data[].postId").type(JsonFieldType.NUMBER).description("게시글 번호"),
                                        fieldWithPath("data[].thumbnailImage").type(JsonFieldType.STRING).description("시리즈"),
                                        fieldWithPath("data[].title").type(JsonFieldType.STRING).description("시리즈 번호"),
                                        fieldWithPath("data[].summary").type(JsonFieldType.STRING).description("시리즈 이름"),
                                        fieldWithPath("data[].userId").type(JsonFieldType.NUMBER).description("게시글 작성자 번호"),
                                        fieldWithPath("data[].createdAt").type(JsonFieldType.NULL).description("게시글 작성 시간"),
                                        fieldWithPath("data[].commentCount").type(JsonFieldType.NUMBER).description("댓글"),
                                        fieldWithPath("data[].likeCount").type(JsonFieldType.NUMBER).description("댓글 작성자 번호")
                                )
                        )
                );
    }

    @DisplayName("사용자와 시리즈 이름으로 게시글 목록을 과거순으로 반환한다.")
    @Test
    void findSeriesPosts() throws Exception {
        //given
        final long postId = 1l;
        final long userId = 1l;
        PostSliceResponse<PostSummaryResponse> postSliceResponse = getPostSliceResponse(postId, userId);
        when(postService.findPostsByUserAndSeries(any(), any(String.class), any(PageRequest.class))).thenReturn(postSliceResponse);

        //when, then
        mockMvc.perform(get("/api/posts")
                        .param("series", "시리즈")
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(20))
                        .param("sort", "createdAt,asc"))
                .andExpect(status().isOk())
                .andDo(document("posts-find-series",
                                resourceDetails().tag("게시물").description("시리즈별 게시물 조회")
                                        .responseSchema(Schema.schema("PostSliceResponse")),
                                queryParameters(
                                        parameterWithName("series").description("시리즈 이름"),
                                        parameterWithName("page").description("현재 페이지"),
                                        parameterWithName("size").description("페이지 당 게시물 수"),
                                        parameterWithName("sort").description("정렬 기준")
                                ),
                                responseFields(
                                        fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("게시글 수"),
                                        fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN).description("마지막 페이지 체크"),
                                        fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("게시글 데이터"),
                                        fieldWithPath("data[].postId").type(JsonFieldType.NUMBER).description("게시글 번호"),
                                        fieldWithPath("data[].thumbnailImage").type(JsonFieldType.STRING).description("시리즈"),
                                        fieldWithPath("data[].title").type(JsonFieldType.STRING).description("시리즈 번호"),
                                        fieldWithPath("data[].summary").type(JsonFieldType.STRING).description("시리즈 이름"),
                                        fieldWithPath("data[].userId").type(JsonFieldType.NUMBER).description("게시글 작성자 번호"),
                                        fieldWithPath("data[].createdAt").type(JsonFieldType.NULL).description("게시글 작성 시간"),
                                        fieldWithPath("data[].commentCount").type(JsonFieldType.NUMBER).description("댓글"),
                                        fieldWithPath("data[].likeCount").type(JsonFieldType.NUMBER).description("댓글 작성자 번호")
                                )
                        )
                );
                .andDo(document("posts-find-series",
                                resourceDetails().tag("게시물").description("시리즈별 게시물 조회")
                                        .responseSchema(Schema.schema("PostSliceResponse")),
                                queryParameters(
                                        parameterWithName("series").description("시리즈 이름"),
                                        parameterWithName("page").description("현재 페이지"),
                                        parameterWithName("size").description("페이지 당 게시물 수"),
                                        parameterWithName("sort").description("정렬 기준")
                                ),
                                responseFields(
                                        fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("게시글 수"),
                                        fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN).description("마지막 페이지 체크"),
                                        fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("게시글 데이터"),
                                        fieldWithPath("data[].postId").type(JsonFieldType.NUMBER).description("게시글 번호"),
                                        fieldWithPath("data[].thumbnailImage").type(JsonFieldType.STRING).description("시리즈"),
                                        fieldWithPath("data[].title").type(JsonFieldType.STRING).description("시리즈 번호"),
                                        fieldWithPath("data[].summary").type(JsonFieldType.STRING).description("시리즈 이름"),
                                        fieldWithPath("data[].userId").type(JsonFieldType.NUMBER).description("게시글 작성자 번호"),
                                        fieldWithPath("data[].createdAt").type(JsonFieldType.NULL).description("게시글 작성 시간"),
                                        fieldWithPath("data[].commentCount").type(JsonFieldType.NUMBER).description("댓글"),
                                        fieldWithPath("data[].likeCount").type(JsonFieldType.NUMBER).description("댓글 작성자 번호")
                                )
                        )
                );
    }

    @DisplayName("게시물 목록을 최신 순서로 조회한다.")
    @Test
    void findRecentPosts() throws Exception {
        //given
        final long postId = 1l;
        final long userId = 1l;
        PostSliceResponse<PostSummaryResponse> postSliceResponse = getPostSliceResponse(postId, userId);
        when(postService.findPostsInLatestOrder(any(PageRequest.class))).thenReturn(postSliceResponse);

        //when, then
        mockMvc.perform(get("/api/posts/recent")
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(20))
                        .param("sort", "createdAt,desc")
                        .header("AuthorizationCode", "tmp"))
                .andExpect(status().isOk())
                .andDo(document("posts-find-recent",
                                resourceDetails().tag("게시물").description("게시물 최근 조회")
                                        .responseSchema(Schema.schema("PostSliceResponse")),
                                queryParameters(
                                        parameterWithName("page").description("현재 페이지"),
                                        parameterWithName("size").description("페이지 당 게시물 수"),
                                        parameterWithName("sort").description("정렬 기준")
                                ),
                                responseFields(
                                        fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("게시글 수"),
                                        fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN).description("마지막 페이지 체크"),
                                        fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("게시글 데이터"),
                                        fieldWithPath("data[].postId").type(JsonFieldType.NUMBER).description("게시글 번호"),
                                        fieldWithPath("data[].thumbnailImage").type(JsonFieldType.STRING).description("시리즈"),
                                        fieldWithPath("data[].title").type(JsonFieldType.STRING).description("시리즈 번호"),
                                        fieldWithPath("data[].summary").type(JsonFieldType.STRING).description("시리즈 이름"),
                                        fieldWithPath("data[].userId").type(JsonFieldType.NUMBER).description("게시글 작성자 번호"),
                                        fieldWithPath("data[].createdAt").type(JsonFieldType.NULL).description("게시글 작성 시간"),
                                        fieldWithPath("data[].commentCount").type(JsonFieldType.NUMBER).description("댓글"),
                                        fieldWithPath("data[].likeCount").type(JsonFieldType.NUMBER).description("댓글 작성자 번호")
                                )
                        )
                );
    }

    @DisplayName("게시물 목록을 최신 순서로 조회한다.")
    @Test
    void findRecentPosts() throws Exception {
        //given
        final long postId = 1l;
        final long userId = 1l;
        PostSliceResponse<PostSummaryResponse> postSliceResponse = getPostSliceResponse(postId, userId);
        when(postService.findPostsInLatestOrder(any(PageRequest.class))).thenReturn(postSliceResponse);

        //when, then
        mockMvc.perform(get("/api/posts/recent")
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(20))
                        .param("sort", "createdAt,desc")
                        .header("AuthorizationCode", "tmp"))
                .andExpect(status().isOk())
                .andDo(document("posts-find-recent",
                                resourceDetails().tag("게시물").description("게시물 최근 조회")
                                        .responseSchema(Schema.schema("PostSliceResponse")),
                                queryParameters(
                                        parameterWithName("page").description("현재 페이지"),
                                        parameterWithName("size").description("페이지 당 게시물 수"),
                                        parameterWithName("sort").description("정렬 기준")
                                ),
                                responseFields(
                                        fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("게시글 수"),
                                        fieldWithPath("hasNext").type(JsonFieldType.BOOLEAN).description("마지막 페이지 체크"),
                                        fieldWithPath("data[]").type(JsonFieldType.ARRAY).description("게시글 데이터"),
                                        fieldWithPath("data[].postId").type(JsonFieldType.NUMBER).description("게시글 번호"),
                                        fieldWithPath("data[].thumbnailImage").type(JsonFieldType.STRING).description("시리즈"),
                                        fieldWithPath("data[].title").type(JsonFieldType.STRING).description("시리즈 번호"),
                                        fieldWithPath("data[].summary").type(JsonFieldType.STRING).description("시리즈 이름"),
                                        fieldWithPath("data[].userId").type(JsonFieldType.NUMBER).description("게시글 작성자 번호"),
                                        fieldWithPath("data[].createdAt").type(JsonFieldType.NULL).description("게시글 작성 시간"),
                                        fieldWithPath("data[].commentCount").type(JsonFieldType.NUMBER).description("댓글"),
                                        fieldWithPath("data[].likeCount").type(JsonFieldType.NUMBER).description("댓글 작성자 번호")
                                )
                        )
                );
    }

    @DisplayName("사용자의 게시물 업데이트 요청을 받아 업데이트 한 뒤 204 상태코드를 반환한다.")
    @Test
    void updatePostTest() throws Exception {
        final Long postId = 1L;
        PostUpdateRequest request = getPostUpdateRequest();
        doNothing().when(postService).updatePost(any(PostUpdateRequest.class), anyLong(), anyLong());

        mockMvc.perform(put("/api/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("AuthrizationCode", "asdasd")
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

    private PostResponse getPostResponse(
            long postId,
            long seriesId,
            long userId,
            long commentId) {
        return new PostResponse(
                postId,
                new SeriesResponse(seriesId, "시리즈"),
                userId,
                "제목",
                0l,
                "내용",
                "요약",
                true,
                "경로",
                null,
                List.of(new CommentResponse(
                        commentId,
                        new Writer(userId, "닉네임"),
                        "내용", null)
                )
        );
    }

    private PostSliceResponse<PostSummaryResponse> getPostSliceResponse(long postId, long userId) {
        return new PostSliceResponse<PostSummaryResponse>(
                10,
                false,
                List.of(new PostSummaryResponse(
                        postId,
                        "썸네일 이미지",
                        "제목",
                        "요약",
                        userId,
                        null,
                        0,
                        0)
                )
        );
    }

    private PostResponse getPostResponse(
            long postId,
            long seriesId,
            long userId,
            long commentId) {
        return new PostResponse(
                postId,
                new SeriesResponse(seriesId, "시리즈"),
                userId,
                "제목",
                0l,
                "내용",
                "요약",
                true,
                "경로",
                null,
                List.of(new CommentResponse(
                        commentId,
                        new Writer(userId, "닉네임"),
                        "내용", null)
                )
        );
    }

    private PostSliceResponse<PostSummaryResponse> getPostSliceResponse(long postId, long userId) {
        return new PostSliceResponse<PostSummaryResponse>(
                10,
                false,
                List.of(new PostSummaryResponse(
                        postId,
                        "썸네일 이미지",
                        "제목",
                        "요약",
                        userId,
                        null,
                        0,
                        0)
                )
        );
    }

}
