package com.youchunmaru.util

import com.youchunmaru.db.service.UserService
import com.youchunmaru.db.service.app.GroupService
import com.youchunmaru.db.service.app.SectionService
import org.jetbrains.exposed.sql.Column


enum class DBPermissions(val subPermissions: List<String> = emptyList()) {
    USERS(),
    SECTIONS(),
    ROLES(),
    PERMISSIONS(),
    MEMBERS(),
    GROUPS(),
    EVENTS(),
    ACCOUNTING();

    val crudPermissions = CRUDPermissions.entries
}

enum class CRUDPermissions() {
    CREATE,
    READ,
    UPDATE,
    DELETE
}

enum class StringFilter(val column: Column<String>) {
    SECTION_NAME(SectionService.Sections.name),
    GROUP_NAME(GroupService.Groups.name),
}