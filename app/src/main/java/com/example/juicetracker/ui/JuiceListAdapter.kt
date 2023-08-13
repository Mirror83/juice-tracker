/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.juicetracker.ui

import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.juicetracker.R
import com.example.juicetracker.data.Juice
import com.example.juicetracker.data.JuiceColor


class JuiceListAdapter(
    private var onEdit: (Juice) -> Unit,
    private var onDelete: (Juice) -> Unit
) : ListAdapter<Juice, JuiceListAdapter.JuiceListViewHolder>(JuiceDiffCallback()) {

    class JuiceListViewHolder(
        private val composeView: ComposeView,
        private val onEdit: (Juice) -> Unit,
        private val onDelete: (Juice) -> Unit
    ) : RecyclerView.ViewHolder(composeView) {

        fun bind(juice: Juice) {
            composeView.setContent {
                ListItem(
                    juice = juice,
                    onDelete = onDelete,
                    Modifier
                        .fillMaxWidth()
                        .clickable { onEdit(juice) }
                        .padding(horizontal = 8.dp, vertical = 16.dp)
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = JuiceListViewHolder(
        ComposeView(parent.context),
        onEdit,
        onDelete
    )

    override fun onBindViewHolder(holder: JuiceListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class JuiceDiffCallback : DiffUtil.ItemCallback<Juice>() {
    override fun areItemsTheSame(oldItem: Juice, newItem: Juice): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Juice, newItem: Juice): Boolean {
        return oldItem == newItem
    }
}

@Composable
fun ListItem(
    juice: Juice,
    onDelete: (Juice) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        JuiceIcon(color = juice.color)
        JuiceDetails(
            name = juice.name,
            description = juice.description,
            rating = juice.rating,
            modifier = Modifier.weight(1f)
        )
        DeleteIcon(onDelete = { onDelete(juice) })
    }

}

@Composable
fun JuiceIcon(color: String, modifier: Modifier = Modifier) {
    val colorLabelMap: Map<String, JuiceColor> =
        JuiceColor.values().associateBy { it.name }
    val selectedColor = Color(colorLabelMap.getValue(color).color)
    val juiceIconContentDescription = stringResource(id = R.string.juice_color, color)

    Box(
        modifier = modifier
            .semantics { contentDescription = juiceIconContentDescription }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_juice_color),
            contentDescription = null,
            tint = selectedColor,
            modifier = Modifier.align(Alignment.Center)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_juice_clear),
            contentDescription = null,
        )

    }
}

@Composable
fun JuiceDetails(
    name: String,
    description: String,
    rating: Int,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = modifier
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(description)
        RatingDisplay(rating = rating, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun RatingDisplay(rating: Int, modifier: Modifier = Modifier) {
    val displayDescription = pluralStringResource(id = R.plurals.number_of_stars, count = rating)

    Row(modifier = modifier.semantics { contentDescription = displayDescription }) {
        repeat(rating) {
            // Star [contentDescription] is null as the image is for illustrative purpose
            Image(
                modifier = Modifier.size(32.dp),
                painter = painterResource(R.drawable.baseline_star_24),
                contentDescription = null
            )
        }

    }
}

@Composable
fun DeleteIcon(onDelete: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = { onDelete() }, modifier) {
        Icon(
            painter = painterResource(id = R.drawable.ic_delete),
            contentDescription = stringResource(id = R.string.delete)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ListItemPreview() {
    val juice = Juice(1, "Apple", "Made from red apples", "Red", 4)
    ListItem(juice = juice, onDelete = {})
}

@Preview(showBackground = true)
@Composable
fun DeleteIconPreview() {
    DeleteIcon(onDelete = { })
}

@Preview(showBackground = true)
@Composable
fun JuiceIconPreview() {
    JuiceIcon(color = "Yellow")
}

@Preview(showBackground = true)
@Composable
fun JuiceDetailsPreview() {
    val juice = Juice(1, "Apple", "Made from red apples", "Red", 4)
    JuiceDetails(name = juice.name, description = juice.description, rating = juice.rating)
}


