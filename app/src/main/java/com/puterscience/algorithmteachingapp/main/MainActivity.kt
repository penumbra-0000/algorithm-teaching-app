package com.puterscience.algorithmteachingapp.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.puterscience.algorithmteachingapp.database.db_classes.DatasetDatabase
import com.puterscience.algorithmteachingapp.database.settings_db.SettingsDatabase
import com.puterscience.algorithmteachingapp.functions.loadSettings
import com.puterscience.algorithmteachingapp.settings.settings_classes.ColourMode
import com.puterscience.algorithmteachingapp.settings.settings_classes.Defaults
import com.puterscience.algorithmteachingapp.settings.settings_classes.Settings
import com.puterscience.algorithmteachingapp.navigation.NavigationDrawer
import com.puterscience.algorithmteachingapp.ui.theme.ApplicationTeachingAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationTeachingAppTheme {
                val persistentDb = Room.databaseBuilder(
                    applicationContext,
                    DatasetDatabase::class.java, "datasets"
                ).allowMainThreadQueries().build()

                val savedSettings = Room.databaseBuilder(
                    applicationContext,
                    SettingsDatabase::class.java, "settings"
                ).allowMainThreadQueries().build()

                val universalNavController = rememberNavController()
                var firstTimeFlag = remember { mutableStateOf(true) }
                val colourModeStd: ColourMode = ColourMode(Color.LightGray, Color.Gray, Color(34, 139, 34), Color.Red, Color.Black)
                val colourModeProDeu: ColourMode = ColourMode(Color.LightGray, Color.Gray, Color.Blue, Color(228, 208, 10), Color.Black)
                val colourModeTri: ColourMode = ColourMode(Color.LightGray, Color.Gray, Color.Blue, Color(/*PINK*/255, 192, 203), Color.Black)
                val colourModeMono: ColourMode = ColourMode(Color.LightGray, Color.Gray, Color.LightGray, Color.DarkGray, Color.Black)
                val allColourModes = listOf<ColourMode>(colourModeStd, colourModeProDeu, colourModeTri, colourModeMono)

                // Handle loading settings
                val allSavedSettings = savedSettings.settingsDao().getSettings()

                var settingsToUse: Settings = Settings(remember {
                    mutableStateOf(true)
                }, remember { mutableStateOf(colourModeStd) }, remember{ mutableIntStateOf(10)})

                if (allSavedSettings.isEmpty()){
                    // Prompt for text change in MainScreen
                    firstTimeFlag.value = true
                }
                else {
                    firstTimeFlag.value = false
                    val trial = loadSettings(allSavedSettings[0], allColourModes)
                    if (trial != null) {
                        settingsToUse = Settings(
                            remember{ mutableStateOf(trial.largeText)},
                            remember{ mutableStateOf(trial.colourMode)},
                            remember {
                                mutableIntStateOf(trial.maxSize)
                            }
                        )
                    }
                }

                val globalSettings = settingsToUse
                val globalDefaults: Defaults = Defaults(
                    defaultLargeText = 26,
                    defaultSmallText = 16
                )
                val colourModes = listOf<ColourMode>(colourModeStd, colourModeProDeu, colourModeTri, colourModeMono)
                NavigationDrawer(
                    navController = universalNavController,
                    navHostController = universalNavController,
                    globalSettings = globalSettings,
                    globalDefaults = globalDefaults,
                    colourModes = colourModes,
                    persistentDb = persistentDb,
                    firstTimeFlag = firstTimeFlag,
                    settingsDao = savedSettings.settingsDao()
                )
            }
        }
    }
}