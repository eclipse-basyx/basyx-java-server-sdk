#!/usr/bin/env bash

# /*******************************************************************************
#  * Copyright (C) 2026 the Eclipse BaSyx Authors
#  *
#  * Permission is hereby granted, free of charge, to any person obtaining
#  * a copy of this software and associated documentation files (the
#  * "Software"), to deal in the Software without restriction, including
#  * without limitation the rights to use, copy, modify, merge, publish,
#  * distribute, sublicense, and/or sell copies of the Software, and to
#  * permit persons to whom the Software is furnished to do so, subject to
#  * the following conditions:
#  *
#  * The above copyright notice and this permission notice shall be
#  * included in all copies or substantial portions of the Software.
#  *
#  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
#  * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
#  * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
#  * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
#  * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
#  * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
#  * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
#  *
#  * SPDX-License-Identifier: MIT
#  ******************************************************************************/

set -euo pipefail

readonly SCRIPT_DIRECTORY="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
readonly GUARD_SCRIPT="${SCRIPT_DIRECTORY}/check_docker_publication.sh"
readonly WORKFLOW_DIRECTORY="${SCRIPT_DIRECTORY}/.."

assert_guard_result() {
	local description="$1"
	local repository="$2"
	local is_fork="$3"
	local expected="$4"
	local actual

	actual="$(bash "${GUARD_SCRIPT}" "${repository}" "${is_fork}")"
	if [[ "${actual}" != "${expected}" ]]; then
		echo "${description}: expected ${expected}, got ${actual}" >&2
		exit 1
	fi
}

assert_workflow_uses_guard() {
	local workflow="$1"

	if [[ "$(grep -c 'check_docker_publication.sh' "${workflow}")" -ne 1 ]]; then
		echo "${workflow} must invoke the shared Docker publication guard exactly once" >&2
		exit 1
	fi
}

# Release workflow cases
assert_guard_result "release in upstream" "eclipse-basyx/basyx-java-server-sdk" "false" "true"
assert_guard_result "release in upstream marked as fork" "eclipse-basyx/basyx-java-server-sdk" "true" "false"
assert_guard_result "release in fork" "contributor/basyx-java-server-sdk" "true" "false"
assert_guard_result "release in renamed repository" "eclipse-basyx/basyx-java-server-sdk-renamed" "false" "false"

# Snapshot push workflow cases
assert_guard_result "push in upstream" "eclipse-basyx/basyx-java-server-sdk" "false" "true"
assert_guard_result "push in upstream marked as fork" "eclipse-basyx/basyx-java-server-sdk" "true" "false"
assert_guard_result "push in fork" "contributor/basyx-java-server-sdk" "true" "false"
assert_guard_result "push in renamed repository" "eclipse-basyx/basyx-java-server-sdk-renamed" "false" "false"

# Missing event metadata must fail closed.
assert_guard_result "missing fork metadata" "eclipse-basyx/basyx-java-server-sdk" "" "false"
assert_guard_result "missing repository metadata" "" "false" "false"

assert_workflow_uses_guard "${WORKFLOW_DIRECTORY}/docker-milestone-release.yml"
assert_workflow_uses_guard "${WORKFLOW_DIRECTORY}/docker-snapshot-release.yml"

echo "Docker publication guard tests passed"
