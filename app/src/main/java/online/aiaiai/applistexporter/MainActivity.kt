package online.aiaiai.applistexporter

import android.app.Activity
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
// import android.os.Environment // No longer needed for Environment.getExternalStoragePublicDirectory
// import android.provider.Settings // No longer needed for MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
// import androidx.compose.ui.platform.LocalContext // Not directly used in this simplified version
import androidx.compose.ui.tooling.preview.Preview
import online.aiaiai.applistexporter.ui.theme.AppListExporterTheme
// import java.io.File // No longer creating File directly in public storage
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : ComponentActivity() {

    private val createAppListFileLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain")
    ) { uri: Uri? ->
        if (uri != null) {
            writeAppListToFile(uri)
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show()
        }
    }

    // requestPermissionLauncher and requestManageStorageLauncher are no longer needed
    // if we exclusively use SAF for file export.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppListExporterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ExportAppListScreen(
                        modifier = Modifier.padding(innerPadding),
                        onExportClick = {
                            // Directly launch the SAF file creation intent
                            createAppListFileLauncher.launch("AppList.txt")
                        }
                    )
                }
            }
        }
    }

    // checkAndRequestPermissions() is no longer needed as we are using SAF

    private fun getAppListString(): String {
        val packageManager = packageManager
        val applications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val appListString = StringBuilder()
        for (appInfo in applications) {
            if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
                val appName = packageManager.getApplicationLabel(appInfo).toString()
                val packageName = appInfo.packageName
                appListString.append("App Name: $appName, Package Name: $packageName\n")
            }
        }
        return appListString.toString()
    }

    private fun writeAppListToFile(uri: Uri) {
        try {
            contentResolver.openFileDescriptor(uri, "w")?.use { parcelFileDescriptor ->
                FileOutputStream(parcelFileDescriptor.fileDescriptor).use { fos ->
                    fos.write(getAppListString().toByteArray())
                }
            }
            Toast.makeText(this, "App list exported successfully", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to export app list: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun ExportAppListScreen(modifier: Modifier = Modifier, onExportClick: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onExportClick) {
            Text("Export App List")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExportAppListScreenPreview() {
    AppListExporterTheme {
        ExportAppListScreen(onExportClick = {})
    }
}

// DELETE THIS ENTIRE FUNCTION
// @Preview(showBackground = true)
// @Composable
// fun GreetingPreview() {
//     AppListExporterTheme {
//         Greeting("Android")
//     }
// }