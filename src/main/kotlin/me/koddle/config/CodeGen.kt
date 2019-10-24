package me.koddle.config

import com.squareup.kotlinpoet.*
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File

/**
 * TODO: This task's purpose is to make creating controller,
 *       repos, model, helper, migration files, and swagger files easier.
 *       Often a project is going to need a lot scaffolding and time spent 
 *       creating files would be better spent elsewhere.
 *       e.g. gradle codegen product id=string name=string ...
 *       would generate a ProductsController.java, ProductRepo.java, product_migration.sql
 *       ProductsHelper.java, Product.java, ProductsControllerTest.java files.
 */
open class CodeGen: DefaultTask() {
    var type = "all"
        @Input
        get() = field.toString()
        @Option(option="type", description="Setting the type")
        set(value) {
            println("value received for type: $value")
            field = value
        }
    var className: String = "Unknown"
        @Input
        get() = field.toString()
        @Option(option="name", description="Setting the class name")
        set(value) {
            println("value received for className: $value")
            field = value
        }
    var packageName: String = "Unknown"
        @Input
        get() = field.toString()
        @Option(option="packageName", description="Setting the class package")
        set(value) {
            println("value received for className: $value")
            field = value
        }

    @TaskAction
    fun generateFile() {
        val generatedClass = ClassName(packageName, className)
        val fileBuilder = FileSpec.builder("", className)
        val constructor = FunSpec.constructorBuilder().addParameter("name", String::class).build()
        val property = PropertySpec.builder("name", String::class).initializer("name").build()
        val type = TypeSpec.classBuilder(className).primaryConstructor(constructor).addProperty(property).build()
        val fileBuilt = fileBuilder.addType(type).build()

        fileBuilt.writeTo(File(""))

        println("type: $type - name: $className")
    }
}