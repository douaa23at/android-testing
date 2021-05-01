package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi // cause using runBlocking test
@RunWith(AndroidJUnit4::class)// cause androidx test lib
@SmallTest//very small
class TasksDaoTest {

    //Executes each task synchronously using Architecture Components
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule() // cause using Architecture components

    private lateinit var database: ToDoDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
                ApplicationProvider.getApplicationContext(),
                ToDoDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @Test
    fun insertTaskAndGetById() = runBlockingTest {// because we are testing suspend functions
        // Given
        val task = Task("title", "description")
        database.taskDao().insertTask(task)
        //When
        val loaded = database.taskDao().getTaskById(task.id)
        // Then
        assertThat<Task>(loaded as Task, notNullValue())
        assertThat(loaded.id, `is`(task.id))
        assertThat(loaded.title, `is`(task.title))
        assertThat(loaded.description, `is`(task.description))
        assertThat(loaded.isCompleted, `is`(task.isCompleted))
    }

    @Test
    fun updateTaskAndGetById() = runBlockingTest {
        val task = Task("title", "description")
        database.taskDao().insertTask(task)
        task.description = "new description"
        database.taskDao().updateTask(task)
        val loaded = database.taskDao().getTaskById(task.id)
        assertThat<Task>(loaded as Task, notNullValue())
        assertThat(loaded.description, `is`("new description"))

    }

    @After
    fun closeDb() {
        database.close()
    }


}