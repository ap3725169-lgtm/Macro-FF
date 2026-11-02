package com.iswipemacro.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iswipemacro.app.model.GestureProfile
import com.iswipemacro.app.ui.theme.*
import com.iswipemacro.app.viewmodel.GestureViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedGesturesSheet(
    viewModel: GestureViewModel,
    onDismiss: () -> Unit,
    onPlayProfile: (GestureProfile) -> Unit
) {
    val savedProfiles by viewModel.savedProfiles.collectAsState()
    val selectedProfile by viewModel.selectedProfile.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var renameTargetId by remember { mutableStateOf<String?>(null) }
    var renameText by remember { mutableStateOf("") }
    var deleteConfirmTargetId by remember { mutableStateOf<String?>(null) }

    val filteredProfiles = savedProfiles.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.category.contains(searchQuery, ignoreCase = true)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkPurpleSurface,
        scrimColor = Color.Black.copy(alpha = 0.7f),
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = CyberCyan.copy(alpha = 0.5f))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Saved Gesture Profiles",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${savedProfiles.size})",
                        color = CyberCyan,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                TextButton(onClick = { viewModel.loadDefaultPresets() }) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Load Presets", color = CyberCyan, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search profiles...", color = Color.Gray, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberCyan,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Profile List
            if (filteredProfiles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (searchQuery.isEmpty()) "No saved profiles yet" else "No matching profiles found",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.loadDefaultPresets() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                        ) {
                            Text("Load Gaming Presets", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredProfiles, key = { it.id }) { profile ->
                        val isSelected = selectedProfile?.id == profile.id
                        val isRenaming = renameTargetId == profile.id
                        val isDeleting = deleteConfirmTargetId == profile.id

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) CyberPurpleCard else DarkPurpleSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) CyberCyan else Color(0x339D00FF),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    viewModel.selectProfile(profile)
                                }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                if (isRenaming) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = renameText,
                                            onValueChange = { renameText = it },
                                            singleLine = true,
                                            modifier = Modifier.weight(1f),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = CyberCyan,
                                                focusedTextColor = Color.White
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = {
                                                if (renameText.isNotBlank()) {
                                                    viewModel.renameProfile(profile.id, renameText.trim())
                                                }
                                                renameTargetId = null
                                            }
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = "Save", tint = CyberCyan)
                                        }
                                        IconButton(onClick = { renameTargetId = null }) {
                                            Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.Gray)
                                        }
                                    }
                                } else {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = profile.name,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    color = Color.White
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Surface(
                                                    color = ElectricViolet.copy(alpha = 0.25f),
                                                    shape = RoundedCornerShape(6.dp),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.5f))
                                                ) {
                                                    Text(
                                                        text = profile.category,
                                                        color = NeonCyanLight,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "${profile.durationMs}ms",
                                                    color = TextMuted,
                                                    fontSize = 11.sp
                                                )
                                                Text("•", color = Color.DarkGray)
                                                Text(
                                                    text = "${profile.totalPoints} pts",
                                                    color = TextMuted,
                                                    fontSize = 11.sp
                                                )
                                                Text("•", color = Color.DarkGray)
                                                Text(
                                                    text = "${profile.totalDistancePx.toInt()}px",
                                                    color = TextMuted,
                                                    fontSize = 11.sp
                                                )
                                                Text("•", color = Color.DarkGray)
                                                Text(
                                                    text = "${profile.averageVelocityPxPerSec.toInt()} px/s",
                                                    color = CyberCyan,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }

                                        // Actions Row
                                        if (isDeleting) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("Delete?", color = DangerRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                IconButton(
                                                    onClick = {
                                                        viewModel.deleteProfile(profile.id)
                                                        deleteConfirmTargetId = null
                                                    }
                                                ) {
                                                    Icon(Icons.Default.Check, contentDescription = "Confirm", tint = DangerRed)
                                                }
                                                IconButton(onClick = { deleteConfirmTargetId = null }) {
                                                    Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.Gray)
                                                }
                                            }
                                        } else {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Button(
                                                    onClick = {
                                                        onPlayProfile(profile)
                                                        onDismiss()
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                    modifier = Modifier.height(34.dp)
                                                ) {
                                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Replay", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }

                                                IconButton(
                                                    onClick = {
                                                        renameTargetId = profile.id
                                                        renameText = profile.name
                                                    }
                                                ) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Rename", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                                                }

                                                IconButton(
                                                    onClick = { viewModel.duplicateProfile(profile) }
                                                ) {
                                                    Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                                                }

                                                IconButton(
                                                    onClick = { deleteConfirmTargetId = profile.id }
                                                ) {
                                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
