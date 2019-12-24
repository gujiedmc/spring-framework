/*
 * Copyright 2002-2019 the original author or authors.
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

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * 访问spring bean容器的根接口。bean容器最基础的客户端访问形式，其它接口像是{@link ListableBeanFactory}
 * 和 {@link org.springframework.beans.factory.config.ConfigurableBeanFactory} 实现了特殊的功能
 *
 * 这个接口的实现类持有一些bean的定义，每个bean都能通过唯一的字符串名称来区分，实现类可以根据工厂的配置返回
 * bean的单例或多例实例，除了单例和多例还支持request和session级别的区分。
 *
 * 这个类的关键点时BeanFactory是应用组件的注册中心，也是这些组件的配置中心
 *
 * Spring建议通过依赖注入的方式（推配置）由构造方法或setter方法来构造对象，而不是通过BeanFactory来进行依赖查找
 * 的方式（拉配置）。
 * Spring的依赖注入功能公国BeanFactory和它的子类来实现。
 *
 * 通常BeanFactory会从配置源（LDAP, RDBMS, XML,properties file等）中加载Bean定义，
 * 并使用{@code org.springframework.beans}包来配置Bean。 但是也可用简单的通过java代码来直接创建Bean。
 *
 *
 * 和{@link ListableBeanFactory}相比，{@link HierarchicalBeanFactory}中的所有操作会检查父工厂。
 * 如果在HierarchicalBeanFactory工厂中没有查到指定Bean，会访问父工厂。
 * 这个类型的BeanFactory中的Bean实例应该覆盖父级的同名Bean。
 *
 * 实现类应该尽可能的支持标准Bean的生存周期接口，包括以下接口
 *
 * <li>BeanNameAware's {@code setBeanName}
 * <li>BeanClassLoaderAware's {@code setBeanClassLoader}
 * <li>BeanFactoryAware's {@code setBeanFactory}
 * <li>EnvironmentAware's {@code setEnvironment}
 * <li>EmbeddedValueResolverAware's {@code setEmbeddedValueResolver}
 * <li>ResourceLoaderAware's {@code setResourceLoader}
 * (only applicable when running in an application context)
 * <li>ApplicationEventPublisherAware's {@code setApplicationEventPublisher}
 * (only applicable when running in an application context)
 * <li>MessageSourceAware's {@code setMessageSource}
 * (only applicable when running in an application context)
 * <li>ApplicationContextAware's {@code setApplicationContext}
 * (only applicable when running in an application context)
 * <li>ServletContextAware's {@code setServletContext}
 * (only applicable when running in a web application context)
 * <li>{@code postProcessBeforeInitialization} methods of BeanPostProcessors
 * <li>InitializingBean's {@code afterPropertiesSet}
 * <li>a custom init-method definition
 * <li>{@code postProcessAfterInitialization} methods of BeanPostProcessors
 * </ol>
 *
 * 当BeanFactory关闭时，以下生存周期方法调用：
 * <ol>
 * <li>{@code postProcessBeforeDestruction} methods of DestructionAwareBeanPostProcessors
 * <li>DisposableBean's {@code destroy}
 * <li>a custom destroy-method definition
 * </ol>
 */
public interface BeanFactory {

	/**
	 * Used to dereference a {@link FactoryBean} instance and distinguish it from
	 * beans <i>created</i> by the FactoryBean. For example, if the bean named
	 * {@code myJndiObject} is a FactoryBean, getting {@code &myJndiObject}
	 * will return the factory, not the instance returned by the factory.
	 */
	String FACTORY_BEAN_PREFIX = "&";


	/**
	 * 查找指定的Bean的实例，可能是单例或多例的。
	 * 这个方法可以允许一个Spring的BeanFactory被用来实现单例和原型模式。
	 * 调用者可以持有单例实例对象的引用。
	 * 查询时会将别名转换会相应的规范bean名称。
	 * 没有查到Bean会查询父工厂
	 *
	 * @param name 要获取的Bean name
	 * @return Bean 实例
	 * @throws NoSuchBeanDefinitionException 没查到指定bean
	 * @throws BeansException bean不能被获取
	 */
	Object getBean(String name) throws BeansException;

	/**
	 * 类似{@link #getBean(String)}，但是通过抛出BeanNotOfRequiredTypeException保证了类型安全。
	 *
	 * @throws BeanNotOfRequiredTypeException 查到的Bean和指定类型不匹配
	 */
	<T> T getBean(String name, Class<T> requiredType) throws BeansException;

	/**
	 * 查找指定的Bean的实例，指定构造参数替代默认值。
	 */
	Object getBean(String name, Object... args) throws BeansException;

	/**
	 * 返回唯一满足类型的Bean实例，如果存在。
	 * 这个方法进入{@link ListableBeanFactory}通过类型查找，但是也可能将类型被转成约定的name根据Bean名称进行查询。
	 * 更多获取Bean的方法参考{@link ListableBeanFactory} 和 {@link BeanFactoryUtils}.
	 */
	<T> T getBean(Class<T> requiredType) throws BeansException;

	<T> T getBean(Class<T> requiredType, Object... args) throws BeansException;

	/**
	 * 返回一个特定Bean的提供者，更宽泛的api形式将获取Bean的方式交给开发，而不是直接返回
	 * 包括懒加载，是否包含，唯一性等功能
	 */
	<T> ObjectProvider<T> getBeanProvider(Class<T> requiredType);

	/**
	 * 返回一个特定Bean的提供者，更宽泛的api形式将获取Bean的方式交给开发，而不是直接返回
	 * 包括懒加载，是否包含，唯一性等功能
	 *
	 * requiredType 可以是正常类型声明，不支持Collection类型。需要以编程的方式
	 * @param requiredType type the bean must match; can be a generic type declaration.
	 * Note that collection types are not supported here, in contrast to reflective
	 * injection points. For programmatically retrieving a list of beans matching a
	 * specific type, specify the actual bean type as an argument here and subsequently
	 * use {@link ObjectProvider#orderedStream()} or its lazy streaming/iteration options.
	 * @return a corresponding provider handle
	 * @since 5.1
	 * @see ObjectProvider#iterator()
	 * @see ObjectProvider#stream()
	 * @see ObjectProvider#orderedStream()
	 */
	<T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType);

	/**
	 * 返回工厂是否包含指定名称的bean定义
	 *
	 * 如果有Bean的定义或者单例实例复合Bean名称，无论这个Bean正在创建或是抽象的，延迟加载的...，都会返回true
	 * 因此方法返回true不代表就能从{@link #getBean}中正常获取实例。
	 */
	boolean containsBean(String name);

	/**
	 * 返回是否是一个共享的单例Bean。如果是，则返会会一直是同一个Bean。
	 * 注意：返回false不能明确表示是全局多例的，只是非单例，具体要根据bean的作用域来区分（可能是request/session独立之类）。
	 * 可以用{@link #isPrototype}严格检查是否是完全独立。
	 */
	boolean isSingleton(String name) throws NoSuchBeanDefinitionException;

	/**
	 * 返回是否是完全独立的。
	 * 返回false，不代表是单例的，有可能是作用域单例。
	 * 通过{@link #isSingleton}确定是否是完全单例。
	 */
	boolean isPrototype(String name) throws NoSuchBeanDefinitionException;

	/**
	 * 检查指定名称的Bean类型和指定类型是否匹配
	 */
	boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException;

	/**
	 * 检查指定名称的Bean类型和指定类型是否匹配
	 */
	boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException;

	/**
	 * 确定指定name的bean的类型。更准确的说返回{@link #getBean}返回对象的类型。
	 *
	 * 对一个{@link FactoryBean}来说，它所创建的对象类型通过{@link FactoryBean#getObjectType()}暴露。
	 * 这可能会导致之前为初始化的FactoryBean进行了初始化（详见{@link #getType(String, boolean)}）。
	 *
	 * @return 无法判断时返回null
	 */
	@Nullable
	Class<?> getType(String name) throws NoSuchBeanDefinitionException;

	/**
	 * 在{@link #getType(String)}的基础上添加了allowFactoryBeanInit标志。
	 * 如果没有早期类型信息，会根据标记决定是否进行初始化。
	 */
	@Nullable
	Class<?> getType(String name, boolean allowFactoryBeanInit) throws NoSuchBeanDefinitionException;

	/**
	 * 返回指定Bean Name的所有别名。所有的别名指向相同的Bean。
	 *
	 * 如果指定名称是别名的话，约定俗称的原始名称会和其它别名一起被返回，原始名称在数组的最前面
	 */
	String[] getAliases(String name);

}
