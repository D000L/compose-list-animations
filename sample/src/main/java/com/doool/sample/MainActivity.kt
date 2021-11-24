package com.doool.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.doool.list_animation.animateItemsIndexed
import com.doool.list_animation.listAnimationState
import com.doool.sample.ui.theme.SampleTheme
import java.util.*

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      SampleTheme {
        Sample()
      }
    }
  }
}

@Stable
data class Data(val text: String = "ADF", val color: Color = Color(Random().nextInt(255), Random().nextInt(255), Random().nextInt(255)))


@Composable
fun Sample() {

  var uuids by remember {
    mutableStateOf(
      (0 .. 10).map { Data(it.toString()) }
    )
  }

  fun add() {
    uuids = uuids.plus(
      Data(
        uuids.size.toString(),
        Color(Random().nextInt(255), Random().nextInt(255), Random().nextInt(255))
      )
    )
  }

  var a = -1
  var b = -1

  fun moveRandom() {
    if (uuids.size > 5) {
      val index = Random().nextInt(uuids.size)
      val to = Random().nextInt(uuids.size)
      var item = uuids.get(index)
      uuids = uuids.minus(item)
      uuids = uuids.subList(0, to) + item + uuids.subList(to, uuids.count())
    }
  }

  fun move(index: Int) {
    if (a == -1) {
      a = index
      return
    }
    if (b == -1) {
      b = index
    }
    if (uuids.size > 1) {
      val index = a
      val to = b
      var item = uuids.get(index)
      uuids = uuids.minus(item)
      uuids = uuids.subList(0, to) + item + uuids.subList(to, uuids.count())
    }
    a = -1
    b = -1
  }

  fun remove(uuid: Data) {
    uuids = uuids.minus(uuid)
  }

  fun removeFirst() {
    if (uuids.isEmpty()) {
      return
    }
    uuids = uuids.dropLast(1)
  }

  val state = rememberLazyListState()

  val items = listAnimationState(uuids)

  Column {
    Row(){
      Button(onClick = { add()}) {}
      Button(onClick = { removeFirst()}) {}
      Button(onClick = { moveRandom()}) {}
    }
    LazyColumn(state = state) {
      item {
        Item(item = Data("asdfasf", Color(234, 25, 141)))
      }
      animateItemsIndexed(items, key = { Pair(it.text, it.color) }) { index, item ->
        if (item.text.toInt() % 2 == 0) Item(item, { remove(item) }) { move(index) }
        else Item2(item, { remove(item) }) { move(index) }
      }
    }
  }
}

@Composable
fun Item(item: Data, remove: () -> Unit = {}, function: () -> Unit = {}) {
  Box(
    Modifier
      .background(color = item.color)
      .fillMaxWidth()
      .height(40.dp)
      .clickable { function() }
  ) {
    Row {
      Text(text = item.text)
      Button(onClick = { remove() }) {

      }
    }
  }
}

@Composable
fun Item2(item: Data, remove: () -> Unit = {}, function: () -> Unit = {}) {
  Box(
    Modifier
      .background(color = item.color)
      .fillMaxWidth()
      .height(100.dp)
      .clickable { function() }
  ) {
    Text(text = item.text)
    Button(onClick = { remove() }) {

    }
  }
}