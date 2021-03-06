/*
 * Copyright (c) 2021 codeeng
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package kartoffel.async

import scala.concurrent.{ ExecutionContext, Future, Promise }

class AsyncFuture(implicit executionContext: ExecutionContext) extends Async[Future] {
  override def pure[V](value: V): Future[V] = Future.successful(value)

  override def map[A, B](fa: Future[A])(f: A => B): Future[B] = fa.map(f)

  override def flatMap[A, B](fa: Future[A])(f: A => Future[B]): Future[B] = fa.flatMap(f)

  override def fail[T](t: Throwable): Future[T] = Future.failed(t)

  override def handle[A](callback: (Either[Throwable, A] => Unit) => Unit): Future[A] = {
    val promise = Promise[A]()
    callback {
      case Left(e)  => promise.failure(e)
      case Right(x) => promise.success(x)
    }
    promise.future
  }
}
