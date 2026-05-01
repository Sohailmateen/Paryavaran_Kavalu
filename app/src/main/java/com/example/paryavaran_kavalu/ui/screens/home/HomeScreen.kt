package com.example.paryavaran_kavalu.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────
// Design Tokens
// ─────────────────────────────────────────────
private val ForestGreen   = Color(0xFF2E7D32)
private val LightGreen    = Color(0xFFA5D6A7)
private val DarkGreen     = Color(0xFF1B5E20)
private val AccentRed     = Color(0xFFD32F2F)
private val AccentGreen   = Color(0xFF388E3C)
private val OffWhite      = Color(0xFFF5F5F5)
private val CardWhite     = Color(0xFFFFFFFF)
private val TextPrimary   = Color(0xFF1C1B1F)
private val TextSecondary = Color(0xFF6B7280)

// ─────────────────────────────────────────────
// Root Screen
// ─────────────────────────────────────────────
@Composable
fun HomeScreen(
    userName: String = "Sohail",
    ecoKarmaPoints: Int = 1_240,
    totalReports: Int = 38,
    cleanedCount: Int = 21,
    onReportWasteClick: () -> Unit = {},
    onViewMapClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = OffWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top App Bar
            HomeTopBar(userName = userName)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // Eco-Karma Card
                EcoKarmaCard(points = ecoKarmaPoints)

                // Primary Action Buttons
                ActionButtonsRow(
                    onReportWasteClick = onReportWasteClick,
                    onViewMapClick = onViewMapClick
                )

                // Stats Section
                Text(
                    text = "Your Impact",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )

                StatsRow(totalReports = totalReports, cleanedCount = cleanedCount)

                // Recent Activity Teaser
                RecentActivityCard()

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────
// Top Bar
// ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(userName: String) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Paryavaran Kavalu",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = CardWhite
                    )
                )
                Text(
                    text = "Hello, $userName 👋",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = LightGreen
                    )
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = CardWhite
                )
            }
            IconButton(onClick = {}) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(LightGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.first().toString(),
                        color = ForestGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = ForestGreen
        )
    )
}

// ─────────────────────────────────────────────
// Eco-Karma Points Card
// ─────────────────────────────────────────────
@Composable
private fun EcoKarmaCard(points: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(DarkGreen, ForestGreen, Color(0xFF43A047))
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = "Eco Karma",
                        tint = LightGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Eco-Karma Points",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = LightGreen,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "%,d".format(points),
                    style = MaterialTheme.typography.displayMedium.copy(
                        color = CardWhite,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 56.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = Color(0x33FFFFFF)
                ) {
                    Text(
                        text = "🏅 Community Ranger",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = CardWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress bar toward next level
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Next: Eco Warrior",
                            style = MaterialTheme.typography.labelSmall.copy(color = LightGreen)
                        )
                        Text(
                            text = "1240 / 2000",
                            style = MaterialTheme.typography.labelSmall.copy(color = LightGreen)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { points / 2000f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50.dp)),
                        color = LightGreen,
                        trackColor = Color(0x33FFFFFF)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Action Buttons Row
// ─────────────────────────────────────────────
@Composable
private fun ActionButtonsRow(
    onReportWasteClick: () -> Unit,
    onViewMapClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Report Waste — Primary CTA
        Button(
            onClick = onReportWasteClick,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ForestGreen,
                contentColor = CardWhite
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Report Waste",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        // View Map — Secondary CTA
        OutlinedButton(
            onClick = onViewMapClick,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = ForestGreen
            ),
            border = BorderStroke(1.dp, ForestGreen)
        ) {
            Icon(
                imageVector = Icons.Default.Map,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "View Map",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

// ─────────────────────────────────────────────
// Stats Cards
// ─────────────────────────────────────────────
@Composable
private fun StatsRow(totalReports: Int, cleanedCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Report,
            label = "Total Reports",
            value = totalReports.toString(),
            iconTint = AccentRed,
            iconBackground = Color(0xFFFFEBEE)
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.CheckCircle,
            label = "Cleaned",
            value = cleanedCount.toString(),
            iconTint = AccentGreen,
            iconBackground = Color(0xFFE8F5E9)
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color,
    iconBackground: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextSecondary
                )
            )
        }
    }
}

// ─────────────────────────────────────────────
// Recent Activity Card
// ─────────────────────────────────────────────
@Composable
private fun RecentActivityCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Reports",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                )
                TextButton(onClick = {}) {
                    Text(
                        text = "See All",
                        color = ForestGreen,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            ActivityItem(
                label = "Plastic near MG Road",
                status = "Pending",
                statusColor = AccentRed,
                time = "2h ago"
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = OffWhite)
            ActivityItem(
                label = "Garbage near KR Market",
                status = "Cleaned",
                statusColor = AccentGreen,
                time = "1d ago"
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = OffWhite)
            ActivityItem(
                label = "E-waste at Koramangala",
                status = "Pending",
                statusColor = AccentRed,
                time = "3d ago"
            )
        }
    }
}

@Composable
private fun ActivityItem(
    label: String,
    status: String,
    statusColor: Color,
    time: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary),
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = statusColor.copy(alpha = 0.12f)
            ) {
                Text(
                    text = status,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = statusColor,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
            Text(
                text = time,
                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
            )
        }
    }
}

// ─────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────
@Preview(showBackground = true, showSystemUi = true, name = "Home Screen")
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            userName = "Sohail",
            ecoKarmaPoints = 1240,
            totalReports = 38,
            cleanedCount = 21
        )
    }
}
