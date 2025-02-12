---
name: Ticket
about: Ticket for this project
title: "[Ticket]"
labels: backlog
assignees: ''
---

## User Story
> **As a** BaSyx user/developer,  
> **I want** [describe the feature or task],  
> **so that** [describe the value or benefit].

Use this section to clearly outline the user/developer perspective. For instance:  
- _“As a BaSyx developer, I want to configure endpoints dynamically, so that I can easily reuse code in different environments.”_

---

## Rules
- **What:** Specify all rules or constraints that should be followed to consider this ticket done.  
- **When:** Outline any triggers or specific conditions.  
- **How:** Define how these constraints are enforced or tested.

Examples:
- "Inputs must be validated for X and Y."  
- "Ensure consistent naming conventions across modules."

---

## Entry Points
Identify where in the system this feature or fix will be accessed or triggered.  
- Is it an API endpoint?  
- A UI component?  
- A background service?  

Provide details like class names, module names, or specific file paths if relevant.

---

## Acceptance Criteria
List the measurable conditions under which this task is considered complete.  
- [ ] Criterion #1  
- [ ] Criterion #2  
- [ ] Criterion #3  

For example:
- _“A user can successfully create an AAS instance via the configured endpoint, and it should be visible in the UI.”_

---

## Risks and Assumptions
- **Risks**: Potential pitfalls or side effects that might occur when implementing this ticket.
- **Assumptions**: Contextual or technical assumptions that the team is making.

Example:
- “We assume the environment will run on Java 11 or higher.”  
- “Changes to the AAS model might introduce backward compatibility issues.”

---

## References and Notes
- Provide links to any related documentation, design docs, or other GitHub issues.
- Add any additional notes or context that might be helpful for implementation or future reference.

---

## Dependencies and Blockers
- **Dependencies**: Other tasks or components this ticket relies on.  
- **Blockers**: Anything preventing work from proceeding, such as waiting on a third-party library or a design decision.

Example:
- “This feature depends on #123 being merged first.”
- “Blocked by design approval from the team lead.”
