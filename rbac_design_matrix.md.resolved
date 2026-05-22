# Role-Based Access Control (RBAC) Permission Matrix

This document outlines the standard permissions assigned to each defined System Role in the Publication Quality Assurance System.

## Roles Defined:
- **ADMIN**: Complete system administrator access.
- **LAB_LEADER**: Manages the entire lab, assigns roles, and oversees operations across all members.
- **SENIOR_RESEARCHER**: Mentors junior researchers, engages in peer reviews, and manages assigned groups.
- **RESEARCHER**: Participates in laboratory activities, creates papers, and responds to reviews.
- **AI_QUALITY_ASSISTANT**: AI agent focused on running checks and reports without modifying core human data.

## Module Actions & Permissions

| Permission Name | ADMIN | LAB_LEADER | SENIOR_RESEARCHER | RESEARCHER | AI_QUALITY_ASSISTANT |
|-----------------|-------|------------|-------------------|------------|----------------------|
| **User & Lab Member Management** |
| `USER_CREATE` | ✅ | ❌ | ❌ | ❌ | ❌ |
| `USER_READ` | ✅ | ✅ | ❌ | ❌ | ❌ |
| `USER_UPDATE` | ✅ | ❌ | ❌ | ❌ | ❌ |
| `USER_DELETE` | ✅ | ❌ | ❌ | ❌ | ❌ |
| `ROLE_ASSIGN` | ✅ | ✅ | ❌ | ❌ | ❌ |
| `LAB_MEMBER_CREATE` | ✅ | ✅ | ❌ | ❌ | ❌ |
| `LAB_MEMBER_READ` | ✅ | ✅ | ✅ | ✅ | ❌ |
| `LAB_MEMBER_UPDATE` | ✅ | ✅ | ❌ | ❌ | ❌ |
| `LAB_MEMBER_DELETE` | ✅ | ✅ | ❌ | ❌ | ❌ |
| **Paper Management** |
| `PAPER_CREATE` | ✅ | ✅ | ✅ | ✅ | ❌ |
| `PAPER_READ_OWN` | ✅ | ✅ | ✅ | ✅ | ❌ |
| `PAPER_READ_ALL` | ✅ | ✅ | ❌ | ❌ | ✅ |
| `PAPER_UPDATE_OWN` | ✅ | ✅ | ✅ | ✅ | ❌ |
| `PAPER_UPDATE_ALL` | ✅ | ✅ | ❌ | ❌ | ❌ |
| `PAPER_DELETE_OWN` | ✅ | ✅ | ✅ | ✅ | ❌ |
| `PAPER_DELETE_ALL` | ✅ | ✅ | ❌ | ❌ | ❌ |
| `PAPER_SUBMIT_INTERNAL_REVIEW` | ✅ | ✅ | ✅ | ✅ | ❌ |
| **Paper Version Management** |
| `PAPER_VERSION_UPLOAD` | ✅ | ✅ | ✅ | ✅ | ❌ |
| `PAPER_VERSION_READ` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `PAPER_VERSION_COMPARE` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `PAPER_VERSION_DELETE` | ✅ | ✅ | ✅ | ✅ | ❌ |
| **Academic Quality & Integrity Checking** |
| `QUALITY_CHECK_RUN` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `QUALITY_REPORT_READ` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `INTEGRITY_CHECK_RUN` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `INTEGRITY_REPORT_READ_OWN` | ✅ | ✅ | ✅ | ✅ | ❌ |
| `INTEGRITY_REPORT_READ_ALL` | ✅ | ✅ | ❌ | ❌ | ✅ |
| `INTEGRITY_REPORT_APPROVE` | ✅ | ✅ | ✅ | ❌ | ❌ |
| **Internal Review** |
| `REVIEW_CREATE` | ✅ | ✅ | ✅ | ❌ | ❌ |
| `REVIEW_READ_ASSIGNED` | ✅ | ✅ | ✅ | ❌ | ❌ |
| `REVIEW_READ_ALL` | ✅ | ✅ | ❌ | ❌ | ❌ |
| `REVIEW_ASSIGN` | ✅ | ✅ | ❌ | ❌ | ❌ |
| `REVIEW_DECISION_SUBMIT` | ✅ | ✅ | ✅ | ❌ | ❌ |
| **Venue Recommendation** |
| `VENUE_RECOMMEND` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `VENUE_CREATE` | ✅ | ❌ | ❌ | ❌ | ❌ |
| `VENUE_UPDATE` | ✅ | ❌ | ❌ | ❌ | ❌ |
| `VENUE_DELETE` | ✅ | ❌ | ❌ | ❌ | ❌ |
| **Submission Tracking** |
| `SUBMISSION_CREATE` | ✅ | ✅ | ✅ | ✅ | ❌ |
| `SUBMISSION_UPDATE` | ✅ | ✅ | ✅ | ✅ | ❌ |
| `SUBMISSION_TRACK` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `SUBMISSION_APPROVE` | ✅ | ✅ | ✅ | ❌ | ❌ |
| **Reviewer Response** |
| `REVIEWER_RESPONSE_CREATE` | ✅ | ✅ | ✅ | ✅ | ❌ |
| `REVIEWER_RESPONSE_READ` | ✅ | ✅ | ✅ | ✅ | ❌ |
| **Notification** |
| `NOTIFICATION_READ` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `NOTIFICATION_MANAGE` | ✅ | ✅ | ❌ | ❌ | ❌ |
| **Analytics Dashboard** |
| `ANALYTICS_READ_OWN` | ✅ | ✅ | ✅ | ✅ | ❌ |
| `ANALYTICS_READ_LAB` | ✅ | ✅ | ❌ | ❌ | ❌ |
| `ANALYTICS_READ_ALL` | ✅ | ❌ | ❌ | ❌ | ❌ |
| **Knowledge Base** |
| `KNOWLEDGE_BASE_CREATE` | ✅ | ✅ | ✅ | ❌ | ❌ |
| `KNOWLEDGE_BASE_READ` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `KNOWLEDGE_BASE_UPDATE` | ✅ | ✅ | ✅ | ❌ | ❌ |
| `KNOWLEDGE_BASE_DELETE` | ✅ | ✅ | ❌ | ❌ | ❌ |
| **Admin Settings & Audit Log** |
| `ADMIN_SETTING_MANAGE` | ✅ | ❌ | ❌ | ❌ | ❌ |
| `AUDIT_LOG_READ` | ✅ | ❌ | ❌ | ❌ | ❌ |

> **Notes:**
> - `ADMIN` intrinsically spans all boundaries without specific individual limits.
> - `LAB_LEADER` permissions encompass laboratory-scale entity and data handling.
> - Details regarding data filtration inherently use database-level scope limitations on top of action checking.
