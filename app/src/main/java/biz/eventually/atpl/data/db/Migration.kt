package biz.eventually.atpl.data.db

import io.realm.*


/**
 * Created by Thibault de Lambilly on 08/06/17.
 */
fun checkRealmVersion() {

    val migration : RealmMigration = RealmMigration { realm: DynamicRealm?, oldVersion: Long, _: Long ->

        var old: Int = oldVersion.toInt()
        realm?.let {
            val schema = it.schema

            // Migrate to version 1: Add a new class.
            if (old == 0) {
                schema.get("Focus")?.addField("topicId", Int::class.javaPrimitiveType)
                old++
            }
        }
    }

    RealmConfiguration.Builder()
            .schemaVersion(1)
            .migration(migration)
            .build()
}
