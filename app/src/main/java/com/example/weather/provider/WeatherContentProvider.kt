package com.example.weather.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.weather.app.AppWeather.Companion.getHistoryDao
import com.example.weather.model.room.HistoryDao
import com.example.weather.model.room.HistoryEntity

private const val URI_ALL = 1 // URI для всех записей
private const val URI_ID = 2 // URI для конкретной записи
private const val ENTITY_PATH =
    "HistoryEntity" // Часть пути (будем определять путь до HistoryEntity)
private const val AUTHORITIES = "weather.provider"

class WeatherContentProvider : ContentProvider() {

    private var authorities: String? = null // Адрес URI
    private lateinit var uriMatcher: UriMatcher // Помогает определить тип адреса URI

    // Типы данных
    private var entityContentType: String? = null // Набор строк
    private var entityContentItemType: String? = null // Одна строка
    private lateinit var contentUri: Uri // Адрес URI Provider

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        require(uriMatcher.match(uri) == URI_ID) { "Wrong URI: $uri" }
        // Получаем доступ к данным
        val historyDao = getHistoryDao()
        // Получаем идентификатор записи по адресу
        val id = ContentUris.parseId(uri)
        // Удаляем запись по идентификатору
        historyDao.deleteHistoryEntityById(id)
        // Нотификация на изменение Cursor
        context?.contentResolver?.notifyChange(uri, null)
        return 1
    }

    override fun getType(uri: Uri): String? {
        when (uriMatcher.match(uri)) {
            URI_ALL -> return entityContentType
            URI_ID -> return entityContentItemType
        }
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        require(uriMatcher.match(uri) == URI_ALL) { "Wrong URI: $uri" }
        // Получаем доступ к данным
        val historyDao = getHistoryDao()
        // Добавляем запись о городе
        val entity = map(values)
        val id: Long = entity.id
        historyDao.insertHistoryEntity(entity)
        val resultUri = ContentUris.withAppendedId(contentUri, id)
        // Уведомляем ContentResolver, что данные по адресу resultUri изменились
        context?.contentResolver?.notifyChange(resultUri, null)
        return resultUri
    }

    override fun onCreate(): Boolean {
        // Прочитаем часть пути из ресурсов
        authorities = AUTHORITIES
        // Вспомогательный класс для определения типа запроса
        uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        // Если нас интересуют все объекты
        uriMatcher.addURI(authorities, ENTITY_PATH, URI_ALL)
        // Если нас интересует только один объект
        uriMatcher.addURI(authorities, "$ENTITY_PATH/#", URI_ID)
        // Тип содержимого — все объекты
        entityContentType =
            "vnd.android.cursor.dir/vnd.$authorities.$ENTITY_PATH"
        // Тип содержимого — один объект
        entityContentItemType =
            "vnd.android.cursor.item/vnd.$authorities.$ENTITY_PATH"
        // Строка для доступа к Provider
        contentUri = Uri.parse("content://$authorities/$ENTITY_PATH")

        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor {
        // Получаем доступ к данным
        val historyDao: HistoryDao = getHistoryDao()
        // При помощи UriMatcher определяем, запрашиваются все элементы или один
        val cursor = when (uriMatcher.match(uri)) {
            URI_ALL -> historyDao.getCursorHistoryEntity() // Запрос к базе данных для всех элементов
            URI_ID -> {
        // Определяем id из URI адреса. Класс ContentUris помогает это сделать
                val id = ContentUris.parseId(uri)
        // Запрос к базе данных для одного элемента
                historyDao.getCursorHistoryEntity(id)
            }
            else -> throw IllegalArgumentException("Wrong URI: $uri")
        }
        // Устанавливаем нотификацию при изменении данных в content_uri
        cursor.setNotificationUri(context!!.contentResolver, contentUri)
        return cursor
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        require(uriMatcher.match(uri) == URI_ID) { "Wrong URI: $uri" }
        // Получаем доступ к данным
        val historyDao = getHistoryDao()
        historyDao.updateHistoryEntity(map(values))
        context!!.contentResolver.notifyChange(uri, null)
        return 1
    }

    // Переводим ContentValues в HistoryEntity
    private fun map(values: ContentValues?): HistoryEntity {
        return if (values == null) {
            HistoryEntity()
        } else {
            val id = if (values.containsKey("id")) values["id"] as Long else 0
            val cityName = values["cityName"] as String
            val temperature = values["temperature"] as Double
            val condition = values["condition"] as String
            HistoryEntity(id, cityName, temperature, condition)
        }
    }

}