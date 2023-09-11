package com.amaurypm.videogamesdb.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.amaurypm.videogamesdb.R
import com.amaurypm.videogamesdb.data.db.model.GameEntity
import com.amaurypm.videogamesdb.databinding.GameElementBinding

class GameAdapter(private val onGameClick: (GameEntity) -> Unit) :
    RecyclerView.Adapter<GameAdapter.ViewHolder>() {

    private var games: List<GameEntity> = emptyList()
    private lateinit var context: Context

    class ViewHolder(private val binding: GameElementBinding) : RecyclerView.ViewHolder(binding.root) {
        val ivIcon: ImageView = binding.ivIcon

        fun bind(context: Context, game: GameEntity) {
            binding.apply {
                tvTitle.text = game.title
                tvDate.text = game.date
                tvDirector.text = game.director
                tvGanre.text = game.genre

                // Configura el ImageView (ivIcon) en función del género del juego
                val genreResource = when (game.genre) {
                    context.getString(R.string.genre_action) -> R.drawable.icon_accion
                    context.getString(R.string.genre_adventure) -> R.drawable.icon_aventura
                    context.getString(R.string.genre_scifi) -> R.drawable.icon_ciencia_ficcion
                    context.getString(R.string.genre_drama) -> R.drawable.icon_drama
                    context.getString(R.string.genre_fantasy) -> R.drawable.icon_fantasia
                    context.getString(R.string.genre_romance) -> R.drawable.icon_romance
                    context.getString(R.string.genre_suspense) -> R.drawable.icon_suspenso
                    context.getString(R.string.genre_horror) -> R.drawable.icon_terror
                    else -> R.drawable.gamepad // Icono predeterminado en caso de que no coincida ninguna opción
                }

                ivIcon.setImageResource(genreResource)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            GameElementBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = games.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(context, games[position])

        holder.itemView.setOnClickListener {
            // Aquí va el click del elemento
            onGameClick(games[position])
        }

        holder.ivIcon.setOnClickListener {
            // Click para la vista del ImageView con el ícono
        }
    }

    fun updateList(list: List<GameEntity>) {
        games = list
        notifyDataSetChanged()
    }
}
