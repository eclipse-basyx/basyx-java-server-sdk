/*******************************************************************************
 * Copyright (C) 2026 the Eclipse BaSyx Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * SPDX-License-Identifier: MIT
 ******************************************************************************/

package org.eclipse.digitaltwin.basyx.submodelrepository.feature.operation.delegation;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Objects;

import io.netty.resolver.AddressResolver;
import io.netty.resolver.AddressResolverGroup;
import io.netty.resolver.InetNameResolver;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import reactor.core.scheduler.Schedulers;

/**
 * Netty {@link AddressResolverGroup} that resolves operation delegation target
 * hosts through the {@link OperationDelegationTargetValidator} at connection
 * time.
 * <p>
 * The addresses returned to the HTTP client are exactly the addresses that
 * passed validation. This closes the time-of-check/time-of-use gap that a
 * separate DNS lookup by the HTTP client would otherwise open (DNS rebinding).
 * Host name resolution may block and is therefore performed off the event
 * loop.
 */
public class OperationDelegationAddressResolverGroup extends AddressResolverGroup<InetSocketAddress> {

	private final OperationDelegationTargetValidator targetValidator;

	public OperationDelegationAddressResolverGroup(OperationDelegationTargetValidator targetValidator) {
		this.targetValidator = Objects.requireNonNull(targetValidator, "targetValidator must not be null");
	}

	@Override
	protected AddressResolver<InetSocketAddress> newResolver(EventExecutor executor) {
		return new ValidatingNameResolver(executor, targetValidator).asAddressResolver();
	}

	private static class ValidatingNameResolver extends InetNameResolver {

		private final OperationDelegationTargetValidator targetValidator;

		ValidatingNameResolver(EventExecutor executor, OperationDelegationTargetValidator targetValidator) {
			super(executor);
			this.targetValidator = targetValidator;
		}

		@Override
		protected void doResolve(String inetHost, Promise<InetAddress> promise) {
			Schedulers.boundedElastic().schedule(() -> {
				try {
					promise.trySuccess(targetValidator.resolveAndValidate(inetHost).get(0));
				} catch (Throwable e) {
					promise.tryFailure(e);
				}
			});
		}

		@Override
		protected void doResolveAll(String inetHost, Promise<List<InetAddress>> promise) {
			Schedulers.boundedElastic().schedule(() -> {
				try {
					promise.trySuccess(targetValidator.resolveAndValidate(inetHost));
				} catch (Throwable e) {
					promise.tryFailure(e);
				}
			});
		}
	}
}
