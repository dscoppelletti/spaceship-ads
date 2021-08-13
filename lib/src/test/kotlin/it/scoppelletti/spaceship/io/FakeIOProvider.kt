package it.scoppelletti.spaceship.io

import java.io.File
import kotlin.io.path.createTempDirectory

class FakeIOProvider : IOProvider {
    private val _dataDir: File = createTempDirectory().toFile()

    override val noBackupFilesDir: File = _dataDir
}
