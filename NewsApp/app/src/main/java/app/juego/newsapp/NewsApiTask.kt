package app.juego.newsapp

import android.os.AsyncTask
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException

class NewsApiTask(private val outputPath: String, private val listener: NewsApiListener) :
    AsyncTask<Void, Void, String>() {

    private val apiKey = "3c4217eff7de46f7a6afaededb1ab2ca" // Reemplaza con tu clave de API de NewsAPI

    interface NewsApiListener {
        fun onNewsApiResult(result: String)
    }

    override fun doInBackground(vararg params: Void): String {
        val url = "https://newsapi.org/v2/top-headlines?country=us&apiKey=$apiKey"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                return "Error: ${response.code}"
            }

            val jsonData = response.body?.string()
            val jsonObject = JSONObject(jsonData)

            val articles = jsonObject.getJSONArray("articles")

            val xmlData = generateXML(articles)

            try {
                val fileWriter = FileWriter(outputPath)
                val bufferedWriter = BufferedWriter(fileWriter)
                bufferedWriter.write(xmlData)
                bufferedWriter.close()
                return "Archivo XML generado exitosamente."
            } catch (e: IOException) {
                e.printStackTrace()
                return "Error al generar el archivo XML."
            }
        }
    }

    override fun onPostExecute(result: String) {
        listener.onNewsApiResult(result)
    }

    private fun generateXML(articles: JSONArray): String {
        val xmlBuilder = StringBuilder()
        xmlBuilder.append("<news>")

        for (i in 0 until articles.length()) {
            val article = articles.getJSONObject(i)
            val title = article.getString("title")
            val description = article.getString("description")
            val url = article.getString("url")

            xmlBuilder.append("<article>")
            xmlBuilder.append("<title>$title</title>")
            xmlBuilder.append("<description>$description</description>")
            xmlBuilder.append("<url>$url</url>")
            xmlBuilder.append("</article>")
        }

        xmlBuilder.append("</news>")

        return xmlBuilder.toString()
    }
}
