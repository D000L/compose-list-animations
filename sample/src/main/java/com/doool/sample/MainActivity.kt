package com.doool.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.doool.list_animation.animateItemsIndexed
import com.doool.list_animation.listAnimationState
import com.doool.sample.ui.theme.SampleTheme
import java.util.*

class MainActivity : ComponentActivity() {
    val viewModel by lazy { SampleViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleTheme {
                Sample(viewModel)
            }
        }
    }
}

@Stable
data class Data(
    val uuid: String = UUID.randomUUID().toString(),
    val color: Color = Color(
        Random().nextInt(255),
        Random().nextInt(255),
        Random().nextInt(255)
    )
)

class SampleViewModel : ViewModel() {
    var uuids by mutableStateOf((0..10).map { Data() })

    fun add() {
        uuids = uuids.plus(Data())
    }

    fun shuffle() {
        uuids = uuids.shuffled()
    }

    fun remove(uuid: Data) {
        uuids = uuids.minus(uuid)
    }

    fun removeLast() {
        if (uuids.isEmpty()) {
            return
        }
        uuids = uuids.dropLast(1)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Sample(viewModel: SampleViewModel) {

    val state = rememberLazyListState()

    val items = listAnimationState(viewModel.uuids)

    Column {
        LazyColumn(
            modifier = Modifier.weight(1f),
            state = state,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            stickyHeader {
                Header()
            }
            animateItemsIndexed(items, key = { it.uuid }) { index, item ->
                Item(item = item, viewModel = viewModel)
            }
        }
        Buttons(viewModel = viewModel)
    }
}

@Composable
private fun Header() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Compose LazyList Diff Animation")
    }
}

@Composable
private fun Item(item: Data, viewModel: SampleViewModel) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(40.dp)
            .background(item.color, shape = CircleShape)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = item.uuid,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Spacer(modifier = Modifier.width(20.dp))
        IconButton(onClick = { viewModel.remove(item) }) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = null)
        }
    }
}

@Composable
private fun Buttons(viewModel: SampleViewModel) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color.White),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = viewModel::add) {
            Text(text = "Push")
        }
        Button(onClick = viewModel::removeLast) {
            Text(text = "Pop")
        }
        Button(onClick = viewModel::shuffle) {
            Text(text = "Shuffle")
        }
    }
}