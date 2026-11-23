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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.digitaltwin.basyx.core.exceptions.OperationDelegationException;
import org.junit.jupiter.api.Test;

import io.netty.resolver.AddressResolver;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Tests the {@link OperationDelegationAddressResolverGroup}, which validates
 * the addresses used for the outbound connection at connection time.
 */
public class TestOperationDelegationAddressResolverGroup {

	private static final InetAddress PUBLIC_ADDRESS = address(93, 184, 216, 34);
	private static final InetAddress LOOPBACK_ADDRESS = address(127, 0, 0, 1);
	private static final InetAddress PRIVATE_ADDRESS = address(10, 0, 0, 1);

	@Test
	public void resolvesPublicAddress() {
		List<InetSocketAddress> resolved = resolveAll(new OperationDelegationSecurityProperties(), host -> new InetAddress[] { PUBLIC_ADDRESS }, "delegation.example", 8080);

		assertEquals(Arrays.asList(new InetSocketAddress(PUBLIC_ADDRESS, 8080)), resolved);
	}

	@Test
	public void rejectsLoopbackAddressAtConnectionTime() {
		Throwable failure = resolveAllFailure(new OperationDelegationSecurityProperties(), host -> new InetAddress[] { LOOPBACK_ADDRESS }, "rebind.example", 8080);

		assertTrue(failure instanceof OperationDelegationException);
	}

	@Test
	public void rejectsWhenAnyResolvedAddressIsBlocked() {
		Throwable failure = resolveAllFailure(new OperationDelegationSecurityProperties(), host -> new InetAddress[] { PUBLIC_ADDRESS, PRIVATE_ADDRESS }, "rebind.example", 8080);

		assertTrue(failure instanceof OperationDelegationException);
	}

	@Test
	public void usesOnlyAddressesFromSingleValidatedLookup() {
		AtomicInteger lookups = new AtomicInteger();
		OperationDelegationTargetValidator.HostResolver rebindingResolver = host -> lookups.getAndIncrement() == 0 ? new InetAddress[] { PUBLIC_ADDRESS } : new InetAddress[] { LOOPBACK_ADDRESS };

		List<InetSocketAddress> resolved = resolveAll(new OperationDelegationSecurityProperties(), rebindingResolver, "rebind.example", 8080);

		assertEquals(Arrays.asList(new InetSocketAddress(PUBLIC_ADDRESS, 8080)), resolved);
		assertEquals(1, lookups.get());
	}

	@Test
	public void allowsLoopbackForAllowlistedHost() {
		OperationDelegationSecurityProperties securityProperties = new OperationDelegationSecurityProperties();
		securityProperties.getAllowlist().setHosts(Arrays.asList("internal.example"));

		List<InetSocketAddress> resolved = resolveAll(securityProperties, host -> new InetAddress[] { LOOPBACK_ADDRESS }, "internal.example", 8080);

		assertEquals(Arrays.asList(new InetSocketAddress(LOOPBACK_ADDRESS, 8080)), resolved);
	}

	@Test
	public void allowsLoopbackForAllowlistedCidr() {
		OperationDelegationSecurityProperties securityProperties = new OperationDelegationSecurityProperties();
		securityProperties.getAllowlist().setCidrs(Arrays.asList("127.0.0.0/8"));

		List<InetSocketAddress> resolved = resolveAll(securityProperties, host -> new InetAddress[] { LOOPBACK_ADDRESS }, "internal.example", 8080);

		assertEquals(Arrays.asList(new InetSocketAddress(LOOPBACK_ADDRESS, 8080)), resolved);
	}

	@Test
	public void resolvesWithoutValidationWhenSecurityIsDisabled() {
		OperationDelegationSecurityProperties securityProperties = new OperationDelegationSecurityProperties();
		securityProperties.setEnabled(false);

		List<InetSocketAddress> resolved = resolveAll(securityProperties, host -> new InetAddress[] { LOOPBACK_ADDRESS }, "internal.example", 8080);

		assertEquals(Arrays.asList(new InetSocketAddress(LOOPBACK_ADDRESS, 8080)), resolved);
	}

	@Test
	public void failsForUnresolvableHost() {
		Throwable failure = resolveAllFailure(new OperationDelegationSecurityProperties(), host -> {
			throw new UnknownHostException(host);
		}, "unknown.example", 8080);

		assertTrue(failure instanceof OperationDelegationException);
	}

	private static List<InetSocketAddress> resolveAll(OperationDelegationSecurityProperties securityProperties, OperationDelegationTargetValidator.HostResolver hostResolver, String host, int port) {
		Future<List<InetSocketAddress>> future = startResolveAll(securityProperties, hostResolver, host, port);
		assertTrue(future.isSuccess(), () -> "Resolution was expected to succeed but failed with " + future.cause());
		return future.getNow();
	}

	private static Throwable resolveAllFailure(OperationDelegationSecurityProperties securityProperties, OperationDelegationTargetValidator.HostResolver hostResolver, String host, int port) {
		Future<List<InetSocketAddress>> future = startResolveAll(securityProperties, hostResolver, host, port);
		assertFalse(future.isSuccess(), "Resolution was expected to fail");
		return future.cause();
	}

	private static Future<List<InetSocketAddress>> startResolveAll(OperationDelegationSecurityProperties securityProperties, OperationDelegationTargetValidator.HostResolver hostResolver, String host, int port) {
		OperationDelegationTargetValidator validator = new OperationDelegationTargetValidator(securityProperties, hostResolver);

		try (OperationDelegationAddressResolverGroup resolverGroup = new OperationDelegationAddressResolverGroup(validator)) {
			AddressResolver<InetSocketAddress> resolver = resolverGroup.getResolver(GlobalEventExecutor.INSTANCE);
			return resolver.resolveAll(InetSocketAddress.createUnresolved(host, port)).awaitUninterruptibly();
		}
	}

	private static InetAddress address(int b0, int b1, int b2, int b3) {
		try {
			return InetAddress.getByAddress(new byte[] { (byte) b0, (byte) b1, (byte) b2, (byte) b3 });
		} catch (UnknownHostException e) {
			throw new IllegalStateException(e);
		}
	}
}
