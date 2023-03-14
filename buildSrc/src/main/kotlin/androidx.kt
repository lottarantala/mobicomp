object androidx {
    object core {
        private val version = "1.7.0"
        val ktx = "androidx.core:core-ktx:$version"
    }
    object compose {
        private val version = "1.1.1"
        object ui {
            val ui = "androidx.compose.ui:ui:$version"
            val tooling = "androidx.compose.ui:ui-tooling-preview:$version"
            val testManifest = "androidx.compose.ui:ui-test-manifest:$version"
        }
        val material = "androidx.compose.material:material:$version"
    }
    object lifecycle {
        private val version = "2.5.1"
        val compose = "androidx.lifecycle:lifecycle-viewmodel-compose:$version"
        val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
    }
    object navigation {
        private val version = "2.5.3"
        val compose = "androidx.navigation:navigation-compose:$version"

        object hilt {
            private val version = "1.0.0"
            val compose = "androidx.hilt:hilt-navigation-compose:$version"
        }
    }
    object activity {
        private val version = "1.3.1"
        val compose = "androidx.activity:activity-compose:$version"
    }
    object room {
        private const val version = "2.5.0"
        val common = "androidx.room:room-common:$version"
        val ktx = "androidx.room:room-ktx:$version"
        val runtime = "androidx.room:room-runtime:$version"
        val compiler = "androidx.room:room-compiler:$version"
    }
    object biometric {
        private const val version = "1.1.0"
        val biometric = "androidx.biometric:biometric:$version"
    }
    object work {
        private val version = "2.8.0"
        val ktx = "androidx.work:work-runtime-ktx:$version"
    }
}