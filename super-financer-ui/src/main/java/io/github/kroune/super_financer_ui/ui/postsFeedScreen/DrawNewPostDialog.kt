package io.github.kroune.super_financer_ui.ui.postsFeedScreen

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import io.github.kroune.super_financer_api.domain.model.NewPostModel
import io.github.kroune.super_financer_ui.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawNewPostDialog(
    title: String,
    isTitleValid: Boolean,
    updateTitle: (String) -> Unit,
    text: String,
    isTextValid: Boolean,
    updateText: (String) -> Unit,
    link: String,
    isLinkValid: Boolean,
    updateLink: (String) -> Unit,
    onDismiss: () -> Unit,
    action: (data: NewPostModel) -> Unit
) {
    Dialog(
        onDismiss,
    ) {
        Card(
            modifier = Modifier
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        updateTitle(it)
                    },
                    label = {
                        Text(stringResource(R.string.title))
                    },
                    placeholder = {
                        Text(stringResource(R.string.title_example))
                    },
                    isError = !isTitleValid && title.isNotEmpty(),
                )
                val textField = rememberTextFieldState(text)
                LaunchedEffect(textField.text) {
                    updateText(textField.text.toString())
                }
                OutlinedTextField(
                    state = textField,
                    label = {
                        Text(stringResource(R.string.text))
                    },
                    placeholder = {
                        Text(stringResource(R.string.text_example))
                    },
                    modifier = Modifier
                        .heightIn(100.dp, 300.dp)
                        .defaultMinSize(minHeight = 70.dp)
                        .verticalScroll(rememberScrollState()),
                    isError = !isTextValid && textField.text.isNotEmpty()
                )
                val images = remember { mutableStateListOf<ByteArray>() }
                if (images.isNotEmpty()) LazyRow(
                    modifier = Modifier
                        .heightIn(max = 100.dp)
                        .clip(RoundedCornerShape(5))
                        .border(3.dp, Color.Black, RoundedCornerShape(5)),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    items(images, { "new-post-image-$it" }) {
                        AsyncImage(
                            model = it,
                            contentDescription = "some useful description",
                            modifier = Modifier.clip(RoundedCornerShape(5))
                        )
                    }
                }
                val context = LocalContext.current
                val pickMultipleMedia = rememberLauncherForActivityResult(
                    ActivityResultContracts.PickMultipleVisualMedia(
                        5
                    )
                ) { uris ->
                    // Callback is invoked after the user selects media items or closes the
                    // photo picker.
                    if (uris.isNotEmpty()) {
                        // 9 375 489
                        uris.forEach {
                            val bytes = context.contentResolver.openInputStream(it)!!.readBytes()
                            images.add(
                                bytes
                            )
                        }
                    } else {
                        Log.d("PhotoPicker", "No media selected")
                    }
                }
                val tags = remember { mutableStateListOf("") }
                val tagsRowScrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .horizontalScroll(tagsRowScrollState),
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // there is an additional 5.dp gap from the arrangement
                    tags.forEachIndexed { index, it ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BasicTextField(
                                    it,
                                    {
                                        tags[index] = it
                                    },
                                    singleLine = true,
                                    textStyle = LocalTextStyle.current.copy(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    ),
                                )
                                IconButton(
                                    { tags.removeAt(index) },
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(2.dp)
                                ) {
                                    Icon(
                                        painterResource(R.drawable.close),
                                        "delete tag",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }
                    }
                    val coroutineScope = rememberCoroutineScope()
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
                        tooltip = { PlainTooltip { Text(stringResource(R.string.add_tags)) } },
                        state = rememberTooltipState(
                            initialIsVisible = true, isPersistent = true
                        )
                    ) {
                        IconButton(
                            {
                                tags.add("")
                                coroutineScope.launch {
                                    tagsRowScrollState.scrollTo(Int.MAX_VALUE)
                                }
                            }, modifier = Modifier.height(20.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.plus),
                                contentDescription = "add new tag",
                                modifier = Modifier.height(20.dp)
                            )
                        }
                    }
                }
                IconButton(
                    {
                        pickMultipleMedia.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }, modifier = Modifier
                        .height(20.dp)
                        .align(Alignment.Start)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add_image),
                        contentDescription = "add new image",
                        modifier = Modifier.size(20.dp)
                    )
                }
                OutlinedTextField(
                    value = link,
                    onValueChange = {
                        updateLink(it)
                    },
                    label = {
                        Text(stringResource(R.string.link))
                    },
                    isError = !isLinkValid && link.isNotEmpty(),
                )
                // this is needed in order to place button perfectly in the center of the height that is left
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button({
                        action(
                            NewPostModel(
                                title,
                                textField.text.toString(),
                                tags,
                                images,
                                link
                            )
                        )
                        onDismiss()
                    },
                        enabled = title.isNotBlank() && isTextValid && tags.all { it.isNotBlank() } && tags.isNotEmpty()) {
                        Text(stringResource(R.string.submit))
                    }
                }
            }
        }
    }
}
