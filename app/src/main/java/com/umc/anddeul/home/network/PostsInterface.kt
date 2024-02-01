import com.umc.anddeul.home.model.Post
import retrofit2.Call
import retrofit2.http.GET

// 홈 게시글 조회 인터페이스
interface PostsInterface {
    @GET("/home/posts")
    fun homePosts() : Call<Post>
}