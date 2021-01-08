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

package kartoffel.async.zio

import kartoffel.async.Async
import zio.Task

class ZIOAsync extends Async[Task] {

  override def pure[V](value: V): Task[V] = Task.succeed(value)

  override def map[A, B](fa: Task[A])(f: A => B): Task[B] = fa.map(f)

  override def flatMap[A, B](fa: Task[A])(f: A => Task[B]): Task[B] = fa.flatMap(f)

  override def fail[T](t: Throwable): Task[T] = Task.fail(t)

  override def handle[A](callback: (Either[Throwable, A] => Unit) => Unit): Task[A] =
    Task.effectAsync[A] { cb =>
      callback {
        case Right(value) => cb(Task.succeed(value))
        case Left(t)      => cb(Task.fail(t))
      }
    }
}
