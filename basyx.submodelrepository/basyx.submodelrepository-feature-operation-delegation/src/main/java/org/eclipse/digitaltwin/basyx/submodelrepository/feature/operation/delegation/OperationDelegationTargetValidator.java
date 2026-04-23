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

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.eclipse.digitaltwin.basyx.core.exceptions.OperationDelegationException;

/**
 * Validates operation delegation targets before outbound HTTP dispatch.
 */
public class OperationDelegationTargetValidator {

	private static final String HTTP = "http";
	private static final String HTTPS = "https";

	private final OperationDelegationSecurityProperties securityProperties;
	private final List<CidrBlock> allowlistedCidrs;
	private final List<String> allowlistedHosts;

	public OperationDelegationTargetValidator(OperationDelegationSecurityProperties securityProperties) {
		this.securityProperties = Objects.requireNonNull(securityProperties, "securityProperties must not be null");
		this.allowlistedHosts = sanitizeHostPatterns(securityProperties.getAllowlist().getHosts());
		this.allowlistedCidrs = parseAllowlistedCidrs(securityProperties.getAllowlist().getCidrs());
	}

	public void validate(URI targetUri) {
		if (!securityProperties.isEnabled())
			return;

		if (targetUri == null)
			throw new OperationDelegationException("Delegation URI must not be null");

		String scheme = normalize(targetUri.getScheme());
		if (!HTTP.equals(scheme) && !HTTPS.equals(scheme)) {
			throw new OperationDelegationException(String.format("Delegation URI scheme '%s' is not supported", targetUri.getScheme()));
		}

		String host = normalize(targetUri.getHost());
		if (host.isEmpty())
			throw new OperationDelegationException("Delegation URI must contain a host");

		int targetPort = resolveTargetPort(targetUri, scheme);
		validatePort(targetPort, targetUri);

		boolean hostAllowlisted = isHostAllowlisted(host);
		InetAddress[] resolvedAddresses = resolveHost(host, targetUri);

		for (InetAddress resolvedAddress : resolvedAddresses) {
			if (hostAllowlisted || isAddressAllowlisted(resolvedAddress))
				continue;

			if (isBlocked(resolvedAddress)) {
				throw new OperationDelegationException(String.format("Delegation target '%s' resolves to blocked address '%s'", targetUri, resolvedAddress.getHostAddress()));
			}
		}
	}

	private int resolveTargetPort(URI targetUri, String scheme) {
		if (targetUri.getPort() > 0)
			return targetUri.getPort();

		return HTTPS.equals(scheme) ? 443 : 80;
	}

	private void validatePort(int targetPort, URI targetUri) {
		List<Integer> allowlistedPorts = securityProperties.getAllowlist().getPorts();
		if (allowlistedPorts == null || allowlistedPorts.isEmpty())
			return;

		if (!allowlistedPorts.contains(targetPort)) {
			throw new OperationDelegationException(String.format("Delegation target '%s' uses blocked port '%d'", targetUri, targetPort));
		}
	}

	private InetAddress[] resolveHost(String host, URI targetUri) {
		try {
			InetAddress[] resolvedAddresses = InetAddress.getAllByName(host);
			if (resolvedAddresses.length == 0)
				throw new OperationDelegationException(String.format("Delegation target '%s' could not be resolved", targetUri));

			return resolvedAddresses;
		} catch (UnknownHostException e) {
			throw new OperationDelegationException(String.format("Delegation target '%s' could not be resolved", targetUri));
		}
	}

	private boolean isHostAllowlisted(String host) {
		if (allowlistedHosts.isEmpty())
			return false;

		for (String hostPattern : allowlistedHosts) {
			if (hostPattern.startsWith("*.")) {
				String suffix = hostPattern.substring(1);
				if (host.endsWith(suffix) && host.length() > suffix.length())
					return true;
				continue;
			}

			if (hostPattern.startsWith(".")) {
				if (host.endsWith(hostPattern) && host.length() > hostPattern.length())
					return true;
				continue;
			}

			if (host.equals(hostPattern))
				return true;
		}

		return false;
	}

	private boolean isAddressAllowlisted(InetAddress address) {
		for (CidrBlock allowlistedCidr : allowlistedCidrs) {
			if (allowlistedCidr.matches(address))
				return true;
		}

		return false;
	}

	private boolean isBlocked(InetAddress address) {
		if (address.isAnyLocalAddress() || address.isMulticastAddress())
			return true;

		if (securityProperties.isDenyLoopbackTargets() && address.isLoopbackAddress())
			return true;

		if (securityProperties.isDenyLinkLocalTargets() && address.isLinkLocalAddress())
			return true;

		if (securityProperties.isDenyPrivateTargets() && isPrivateAddress(address))
			return true;

		if (securityProperties.isDenyMetadataTargets() && isMetadataAddress(address))
			return true;

		return false;
	}

	private boolean isPrivateAddress(InetAddress address) {
		if (address instanceof Inet4Address) {
			byte[] bytes = address.getAddress();
			int b0 = Byte.toUnsignedInt(bytes[0]);
			int b1 = Byte.toUnsignedInt(bytes[1]);

			if (b0 == 10)
				return true;

			if (b0 == 172 && b1 >= 16 && b1 <= 31)
				return true;

			if (b0 == 192 && b1 == 168)
				return true;

			return address.isSiteLocalAddress();
		}

		if (address instanceof Inet6Address) {
			byte[] bytes = address.getAddress();
			return isUniqueLocalIpv6(bytes) || address.isSiteLocalAddress();
		}

		return false;
	}

	private boolean isMetadataAddress(InetAddress address) {
		if (!(address instanceof Inet4Address))
			return false;

		byte[] bytes = address.getAddress();
		return Byte.toUnsignedInt(bytes[0]) == 169 && Byte.toUnsignedInt(bytes[1]) == 254 && Byte.toUnsignedInt(bytes[2]) == 169 && Byte.toUnsignedInt(bytes[3]) == 254;
	}

	private boolean isUniqueLocalIpv6(byte[] bytes) {
		return bytes.length == 16 && (bytes[0] & (byte) 0xFE) == (byte) 0xFC;
	}

	private List<String> sanitizeHostPatterns(List<String> hostPatterns) {
		if (hostPatterns == null || hostPatterns.isEmpty())
			return Collections.emptyList();

		List<String> sanitizedPatterns = new ArrayList<>();
		for (String hostPattern : hostPatterns) {
			String normalizedPattern = normalize(hostPattern);
			if (!normalizedPattern.isEmpty())
				sanitizedPatterns.add(normalizedPattern);
		}

		return sanitizedPatterns;
	}

	private List<CidrBlock> parseAllowlistedCidrs(List<String> cidrs) {
		if (cidrs == null || cidrs.isEmpty())
			return Collections.emptyList();

		List<CidrBlock> parsedCidrs = new ArrayList<>();
		for (String cidr : cidrs) {
			String normalizedCidr = normalize(cidr);
			if (normalizedCidr.isEmpty())
				continue;

			parsedCidrs.add(CidrBlock.parse(normalizedCidr));
		}

		return parsedCidrs;
	}

	private String normalize(String value) {
		if (value == null)
			return "";

		String normalized = value.trim().toLowerCase(Locale.ROOT);
		if (normalized.endsWith("."))
			return normalized.substring(0, normalized.length() - 1);

		return normalized;
	}

	private static class CidrBlock {
		private final byte[] network;
		private final int prefixLength;
		private final int totalBits;

		private CidrBlock(byte[] network, int prefixLength) {
			this.network = network;
			this.prefixLength = prefixLength;
			this.totalBits = network.length * 8;
		}

		static CidrBlock parse(String cidr) {
			String[] parts = cidr.split("/");
			if (parts.length != 2)
				throw new IllegalArgumentException(String.format("Invalid CIDR allowlist entry '%s'", cidr));

			InetAddress baseAddress;
			int prefixLength;
			try {
				baseAddress = InetAddress.getByName(parts[0]);
				prefixLength = Integer.parseInt(parts[1]);
			} catch (Exception e) {
				throw new IllegalArgumentException(String.format("Invalid CIDR allowlist entry '%s'", cidr));
			}

			byte[] addressBytes = baseAddress.getAddress();
			int totalBits = addressBytes.length * 8;
			if (prefixLength < 0 || prefixLength > totalBits)
				throw new IllegalArgumentException(String.format("Invalid CIDR prefix in allowlist entry '%s'", cidr));

			return new CidrBlock(applyMask(addressBytes, prefixLength), prefixLength);
		}

		boolean matches(InetAddress address) {
			byte[] addressBytes = address.getAddress();
			if (addressBytes.length * 8 != totalBits)
				return false;

			byte[] maskedCandidate = applyMask(addressBytes, prefixLength);
			if (maskedCandidate.length != network.length)
				return false;

			for (int i = 0; i < network.length; i++) {
				if (network[i] != maskedCandidate[i])
					return false;
			}

			return true;
		}

		private static byte[] applyMask(byte[] input, int prefixLength) {
			byte[] result = new byte[input.length];
			int remainingBits = prefixLength;

			for (int i = 0; i < input.length; i++) {
				if (remainingBits >= 8) {
					result[i] = input[i];
					remainingBits -= 8;
					continue;
				}

				if (remainingBits <= 0) {
					result[i] = 0;
					continue;
				}

				int mask = (0xFF << (8 - remainingBits)) & 0xFF;
				result[i] = (byte) (input[i] & mask);
				remainingBits = 0;
			}

			return result;
		}
	}
}
