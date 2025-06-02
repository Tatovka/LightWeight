package com.example.lightweight

import com.example.lightweight.database.ExerciseRepo
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*


class RepoTest {
    @Test
    fun addition_isCorrect() = runTest {
        val repo = ExerciseRepo(FakeExDao())
        val data = repo.getAll()[42]
        assertNotNull(data)
        if(data != null) assertEquals(data.name, "sample")
    }
}