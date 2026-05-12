package com.example.paryavaran_kavalu.ui.screens.auth.role

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionScreen(
    onContinueClick: (String) -> Unit
) {
    var selectedRole by remember { mutableStateOf("Citizen") }
    val roles = listOf("Citizen", "Volunteer")
    val ecoGreen = MaterialTheme.colorScheme.primary

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Your Role",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = ecoGreen
            )
            Text(
                text = "Select how you'll help the environment",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Column(
                Modifier
                    .selectableGroup()
                    .fillMaxWidth()
            ) {
                roles.forEach { role ->
                    val isSelected = selectedRole == role
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .selectable(
                                selected = isSelected,
                                onClick = { selectedRole = role },
                                role = Role.RadioButton
                            ),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = if (isSelected) ecoGreen.copy(alpha = 0.1f) else Color.Transparent
                        ),
                        border = BorderStroke(
                            width = 2.dp,
                            color = if (isSelected) ecoGreen else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (role == "Citizen") Icons.Default.Person else Icons.Default.Group,
                                contentDescription = null,
                                tint = if (isSelected) ecoGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = role,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) ecoGreen else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            RadioButton(
                                selected = isSelected,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(selectedColor = ecoGreen)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { onContinueClick(selectedRole) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ecoGreen),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Continue", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoleSelectionScreenPreview() {
    RoleSelectionScreen(onContinueClick = {})
}
