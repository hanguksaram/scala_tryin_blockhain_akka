import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{Matchers, WordSpec}

class TodoRouterListSpec extends WordSpec with Matchers with ScalatestRouteTest with TodoMocks {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  private val doneTodo =
    Todo("1", "Buy milk", "The cat is thirsty!", done = true)
  private val pendingTodo =
    Todo("2", "Buy eggs", "Run out of eggs", done = false)

  private val todos = Seq(doneTodo, pendingTodo)

  "TodoRouter" should {


    "return all the todos" in {
      val repository = new InMemoryTodoRepository(todos)
      val router = new TodoRouter(repository)

      Get("/todos") -> router.route -> check {
        status shouldBe StatusCodes.OK
        val respoonse = responseAs[Seq[Todo]]
        response shouldBe todos
      }

    }

    "return all the done todos" in {
      val repository = new InMemoryTodoRepository(todos)
      val router = new TodoRouter(repository)

      Get("/todos/done") -> router.route -> check {
        status shouldBe StatusCodes.OK
        val respoonse = responseAs[Seq[Todo]]
        response shouldBe Seq(doneTodo)
      }

    }

    "return all the pending todos" in {
      val repository = new InMemoryTodoRepository(todos)
      val router = new TodoRouter(repository)

      Get("/todos/pending") -> router.route -> check {
        status shouldBe StatusCodes.OK
        val respoonse = responseAs[Seq[Todo]]
        response shouldBe Seq(pendingTodo)
      }

    }

    "handle repository fauilure in the todos route" in {
      val repository = new FailingRepository
      val router = new TodoRouter(repository)

      Get("/todos") -> router.route -> check {
        status shouldBe StatusCodes.InternalServerError

      }

    }
    "handle repository fauilure in the todos/done route" in {
      val repository = new FailingRepository
      val router = new TodoRouter(repository)

      Get("/todos/done") -> router.route -> check {
        status shouldBe StatusCodes.InternalServerError

      }

    }
    "handle repository fauilure in the todos/peding route" in {
      val repository = new FailingRepository
      val router = new TodoRouter(repository)

      Get("/todos/pending") -> router.route -> check {
        status shouldBe StatusCodes.InternalServerError

      }

    }
  }
}
