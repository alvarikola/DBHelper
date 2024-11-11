package org.iesharia.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.iesharia.myapplication.data.DBHelper
import org.iesharia.myapplication.ui.theme.MyApplicationTheme



class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                ) { innerPadding ->
                    MainActivity (
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@SuppressLint("Range")
@Composable
fun MainActivity(modifier: Modifier) {
    val context = LocalContext.current
    val db by remember { mutableStateOf(DBHelper(context)) }

    var lName:MutableList<String> = remember { mutableListOf<String>() }
    var lAge:MutableList<String> = remember { mutableListOf<String>() }
    var lId:MutableList<String> = remember { mutableListOf<String>() }
    var mostrarBorrar by remember { mutableStateOf(false) }
    var mostrarEditar by remember { mutableStateOf(false) }
    var mostrarDialogo by remember { mutableStateOf(false) }


    Column (
        verticalArrangement = Arrangement.Center,
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Base de Datos",
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )
        Text(
            text = "Muuuuuy simple\nNombre/Edad",
            fontSize = 10.sp

        )
        //Nombre
        var nameValue by remember { mutableStateOf("") }
        OutlinedTextField(
            value = nameValue,
            onValueChange = {
                nameValue = it
            },
            modifier = Modifier,
            textStyle = TextStyle(color = Color.DarkGray),
            label = { Text(text = "Nombre") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )
        //Edad
        var ageValue by remember { mutableStateOf("") }
        OutlinedTextField(
            value = ageValue,
            onValueChange = {
                ageValue = it
            },
            modifier = Modifier,
            textStyle = TextStyle(color = Color.DarkGray),
            label = { Text(text = "Edad") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )
        var bModifier:Modifier = Modifier.padding(20.dp)
        Row {
            Button(
                modifier = bModifier,
                onClick = {

                    val name = nameValue
                    val age = ageValue

                    db.addName(name, age)

                    Toast.makeText(
                        context,
                        name + " adjuntado a la base de datos",
                        Toast.LENGTH_LONG)
                        .show()

                    nameValue = ""
                    ageValue = ""
                }
            ) {
                Text(text = "Añadir")
            }
            Button(
                modifier = bModifier,
                onClick = {
                    try {
                        lId.clear()
                        lName.clear()
                        lAge.clear()

                        val db = DBHelper(context, null)

                        val cursor = db.getName()

                        cursor!!.moveToFirst()
                        lName.add(cursor.getString(cursor.getColumnIndex(DBHelper.NAME_COl)))
                        lAge.add(cursor.getString(cursor.getColumnIndex(DBHelper.AGE_COL)))
                        lId.add(cursor.getString(cursor.getColumnIndex(DBHelper.ID_COL)))


                        while(cursor.moveToNext()){
                            lName.add(cursor.getString(cursor.getColumnIndex(DBHelper.NAME_COl)))
                            lAge.add(cursor.getString(cursor.getColumnIndex(DBHelper.AGE_COL)))
                            lId.add(cursor.getString(cursor.getColumnIndex(DBHelper.ID_COL)))

                        }

                        cursor?.close()
                    } catch (e: Exception){
                        e.printStackTrace()
                    }
                }
            ) {
                Text(text = "Mostrar")
            }
        }
        Column {
            Row (

            ){
                Text(
                    text = "Nombre"
                )
                Spacer(modifier = Modifier.padding(15.dp))
                Text(
                    text = "Edad"
                )
            }
        }

        for (i in lName.indices) {
            Row {
                ClickableText(
                    text = AnnotatedString(lName[i]) ,
                    onClick = {
                        mostrarBorrar = true
                        mostrarEditar = true
                    },
                    modifier = bModifier
                )
                Text(
                    modifier = bModifier,
                    text = lAge[i]
                )
                if (mostrarBorrar) {
                    Button(
                        modifier = bModifier,
                        onClick = {
                            val fueBorrado = db.deleteName(lId[i])

                            if (fueBorrado) {
                                lName.removeAt(i)
                                lAge.removeAt(i)
                                mostrarBorrar = false // Ocultar el botón de borrar
                                mostrarEditar = false // Ocultar el botón de editar
                                Toast.makeText(context, "Registro eliminado", Toast.LENGTH_SHORT).show()
                            }
                        },
                    ) {
                        Text(
                            text = "Borrar"
                        )
                    }
                }
                if (mostrarEditar) {
                    Button(

                        modifier = bModifier,
                        onClick = {
                            mostrarDialogo = true
                            mostrarBorrar = false // Ocultar el botón de borrar
                            mostrarEditar = false // Ocultar el botón de editar
                        },
                    ) {
                        Text(
                            text = "Editar"
                        )
                    }
                }
                if (mostrarDialogo) {
                    var nameValue by remember { mutableStateOf(lName[i]) }
                    var ageValue by remember { mutableStateOf(lAge[i]) }
                    AlertDialog(
                        onDismissRequest = { mostrarDialogo = false },
                        title = { Text(text = "Actualizar") },
                        text = {
                            Column (
                                verticalArrangement = Arrangement.Center,
                                modifier = modifier,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                //Nombre
                                OutlinedTextField(
                                    value = nameValue,
                                    onValueChange = {
                                        nameValue = it
                                    },
                                    modifier = Modifier,
                                    textStyle = TextStyle(color = Color.DarkGray),
                                    label = { Text(text = "Nombre") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                //Edad
                                OutlinedTextField(
                                    value = ageValue,
                                    onValueChange = {
                                        ageValue = it
                                    },
                                    modifier = Modifier,
                                    textStyle = TextStyle(color = Color.DarkGray),
                                    label = { Text(text = "Edad") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                val fueActualizado = db.updateName(lId[i], nameValue, ageValue)
                                if (fueActualizado) {
                                    mostrarDialogo = false
                                    Toast.makeText(context, "Registro actualizado", Toast.LENGTH_SHORT).show()
                                    Log.i("prueba", nameValue)
                                }
                            }) {
                                Text("Actualizar")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { mostrarDialogo = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }


    }
}
