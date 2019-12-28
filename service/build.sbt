val lib = (project in file("lib"))
val core = (project in file("core")).dependsOn(lib)
