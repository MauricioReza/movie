package com.amaurypm.videogamesdb.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.amaurypm.videogamesdb.R
import com.amaurypm.videogamesdb.application.VideogamesDBApp
import com.amaurypm.videogamesdb.data.GameRepository
import com.amaurypm.videogamesdb.data.db.model.GameEntity
import com.amaurypm.videogamesdb.databinding.GameDialogBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Creado por Amaury Perea Matsumura el 01/09/23
 */
class GameDialog(
    private val newGame: Boolean = true,
    private var game: GameEntity = GameEntity(
        title = "",
        date = "",
        director = "",
        genre = ""
    ),
    private val updateUI: () -> Unit,
    private val message: (String) -> Unit,
    private var selectedGenre: String? = null
) : DialogFragment() {

    private var _binding: GameDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var builder: AlertDialog.Builder
    private lateinit var dialog: Dialog

    private var saveButton: Button? = null

    private lateinit var repository: GameRepository

    // Se configura el diálogo inicial
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = GameDialogBinding.inflate(requireActivity().layoutInflater)

        repository = (requireContext().applicationContext as VideogamesDBApp).repository

        builder = AlertDialog.Builder(requireContext())

        // Configurar campos de texto con datos del juego
        binding.apply {
            tietTitle.setText(game.title)
            tietdate.setText(game.genre)
            tietDirector.setText(game.director)
        }

        // SPINNER
        val datos = arrayListOf(
            "Acción", "Aventura", "Ciencia ficción", "Drama", "Fantasía", "Romance", "Suspenso", "Terror"
        )
        val adaptador = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, datos)

        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genreSpinner.adapter = adaptador

        binding.genreSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val itemSelected = datos[position]
                Toast.makeText(requireContext(), "Item seleccionado: $itemSelected", Toast.LENGTH_SHORT).show()
                saveButton?.isEnabled = validateFields()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Toast.makeText(requireContext(), "Nada seleccionado", Toast.LENGTH_SHORT).show()
                saveButton?.isEnabled = validateFields()
            }
        }

        // Configurar el Spinner (genreSpinner)
        val genreIndex = datos.indexOf(game.genre)
        binding.genreSpinner.setSelection(-1) // Establecer en -1 para que no haya opción seleccionada por defecto

        if (genreIndex >= 0) {
            binding.genreSpinner.setSelection(genreIndex)
        }

        dialog = if (newGame) {
            buildDialog("Guardar", "Cancelar", {
                // Create (Guardar)
                game.title = binding.tietTitle.text.toString()
                game.date = binding.tietdate.text.toString()
                game.director = binding.tietDirector.text.toString()
                game.genre = binding.genreSpinner.selectedItem.toString()

                try {
                    lifecycleScope.launch {
                        repository.insertGame(game)
                    }

                    message(getString(R.string.juego_guardado))

                    // Actualizar la UI
                    updateUI()

                } catch (e: IOException) {
                    e.printStackTrace()
                    message("Error al guardar el juego")
                }
            }, {
                // Cancelar
            })
        } else {
            buildDialog("Actualizar", "Borrar", {
                // Update
                game.title = binding.tietTitle.text.toString()
                game.date = binding.tietdate.text.toString()
                game.director = binding.tietDirector.text.toString()
                game.genre = binding.genreSpinner.selectedItem.toString()

                try {
                    lifecycleScope.launch {
                        repository.updateGame(game)
                    }

                    message("Juego actualizado exitosamente")

                    // Actualizar la UI
                    updateUI()

                } catch (e: IOException) {
                    e.printStackTrace()
                    message("Error al actualizar el juego")
                }

            }, {
                // Delete

                AlertDialog.Builder(requireContext())
                    .setTitle("Confirmación")
                    .setMessage("¿Realmente deseas eliminar el juego ${game.title}?")
                    .setPositiveButton("Aceptar") { _, _ ->
                        try {
                            lifecycleScope.launch {
                                repository.deleteGame(game)
                            }

                            message("Juego eliminado exitosamente")

                            // Actualizar la UI
                            updateUI()

                        } catch (e: IOException) {
                            e.printStackTrace()
                            message("Error al eliminar el juego")
                        }
                    }
                    .setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()

            })
        }

        return dialog
    }

    // Cuando se destruye el fragment
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // Se llama después de que se muestra el diálogo en pantalla
    override fun onStart() {
        super.onStart()

        val alertDialog =
            dialog as AlertDialog // Lo usamos para poder emplear el método getButton (no lo tiene el dialog)
        saveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
        saveButton?.isEnabled = false

        binding.tietTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }
        })

        binding.tietdate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }

        })

        binding.tietDirector.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }

        })

    }

    private fun validateFields() =
        (binding.tietTitle.text.toString().isNotEmpty() &&
                binding.tietdate.text.toString().isNotEmpty() &&
                binding.tietDirector.text.toString().isNotEmpty() &&
                binding.genreSpinner.selectedItemPosition != AdapterView.INVALID_POSITION)

    private fun buildDialog(
        btn1Text: String,
        btn2Text: String,
        positiveButton: () -> Unit,
        negativeButton: () -> Unit
    ): Dialog =
        builder.setView(binding.root)
            .setTitle(R.string.game_dialog_title)
            .setPositiveButton(btn1Text, DialogInterface.OnClickListener { dialog, which ->
                // Acción para el botón positivo
                positiveButton()
            })
            .setNegativeButton(btn2Text) { _, _ ->
                // Acción para el botón negativo
                negativeButton()
            }
            .create()
}
