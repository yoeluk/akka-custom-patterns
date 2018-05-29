package akka.pattern

import java.util.concurrent._

import akka.actor.ActorRef
import akka.util.Timeout

import scala.concurrent.java8.FuturesConvertersImpl.{CF, P}
import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService, Future}

object CustomPatterns {
  import scala.compat.java8.FutureConverters._

  def executorService(service: ExecutorService): ExecutionContextExecutorService =
    fromExecutorService(service)

  def ask(actor: ActorRef, message: Any, timeoutMillis: Long, ec: ExecutionContext): CompletionStage[AnyRef] =
    toJava(ask(actor, message)(new Timeout(timeoutMillis, TimeUnit.MILLISECONDS)), ec).asInstanceOf[CompletionStage[AnyRef]]

  private def ask(actorRef: ActorRef, message: Any)(implicit timeout: Timeout): Future[Any] =
    actorRef.actorAsk(message, timeout, ActorRef.noSender)

  private def toJava[T](f: Future[T], exc: ExecutionContext): CompletionStage[T] = {
    f match {
      case p: P[T] => p.wrapped
      case _ =>
        val cf = new CF[T](f)
        implicit val ec = exc
        f onComplete cf
        cf
    }
  }

  private implicit class RichActor(val actor: ActorRef) extends AnyVal {
    def actorAsk(message: Any, timeout: Timeout, sender: ActorRef): Future[Any] =
      new AskableActorRef(actor).internalAsk(message, timeout, sender)
  }
}
