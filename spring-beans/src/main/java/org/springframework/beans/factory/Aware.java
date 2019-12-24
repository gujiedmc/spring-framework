/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory;

/**
 *
 * 一个标记接口，用来标明一个Bean可以被特定Spring容器通过回调方法方式通知调用，可以感知到Spring的一些信息。
 * 实际的方法签名由各个子接口确定，但通常应该仅由一个接受单个参数的void返回方法组成。
 *
 * 请注意，仅仅是实现Aware没有提供默认功能。 相反，处理必须明确完成，例如在org.springframework.beans.factory.config.BeanPostProcessor 。
 * 参阅org.springframework.context.support.ApplicationContextAwareProcessor 用于处理特定的例子Aware接口回调。
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 */
public interface Aware {

}
