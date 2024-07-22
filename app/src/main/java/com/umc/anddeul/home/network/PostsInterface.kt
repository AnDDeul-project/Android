import com.umc.anddeul.home.model.Post
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

// 홈 게시글 조회 인터페이스
interface PostsInterface {
    @GET("/home/posts/{page}")
    fun homePosts(
        @Path("page") page : Int
    ) : Call<Post>
}