package com.team45.mysustainablecity.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.LatLng
import com.team45.mysustainablecity.Screen
import com.team45.mysustainablecity.ui.components.FilterPill
import com.team45.mysustainablecity.utils.Tag
import com.team45.mysustainablecity.viewmodel.AuthViewModel

data class Post(
    val id: String,                          // unique key — matches MapLocation.name for linked posts
    val username: String,
    val timeAgo: String,
    val title: String,
    val body: String,
    val likes: Int,
    val comments: Int,
    val tags: List<Tag> = emptyList(),
    val hasImage: Boolean = false,
    val imageUrl: String? = null,            // real image URL / resource ref (optional)
    // Map fields — null means post is feed-only (not pinned on map)
    val position: LatLng? = null,
    val description: String = body,          // shown in map bottom sheet; defaults to body
)


// Main screen
@Composable
fun DiscoverScreen(
    authViewModel: AuthViewModel,
    padding: PaddingValues,
    navController: NavController
) {
    var activeFilters by remember { mutableStateOf<Set<Tag>>(emptySet()) }




    val posts = listOf(
        // ── Quayside Bike Racks ──────────────────────────────────────────
        Post(
            id = "Quayside Bike Rack A",
            username = "cycleNewcastle",
            timeAgo = "3 hr. ago",
            title = "Quayside Bike Rack A",
            body = "Bike rack near the Quayside restaurants, 12 spaces available. Great spot for locking up before dinner!",
            likes = 142,
            comments = 18,
            tags = listOf(Tag.BIKE_RACK, Tag.APPROVED),
            position = LatLng(54.9686, -1.6094),
        ),
        Post(
            id = "Quayside Bike Rack B",
            username = "pedal_pete",
            timeAgo = "5 hr. ago",
            title = "Quayside Bike Rack B",
            body = "Bike rack outside the Pitcher & Piano, 8 spaces. Pending council sign-off — fingers crossed!",
            likes = 87,
            comments = 9,
            tags = listOf(Tag.BIKE_RACK, Tag.AWAITING_APPROVAL),
            position = LatLng(54.9688, -1.6088),
        ),
        Post(
            id = "Quayside Bike Rack C",
            username = "twoWheelsTyne",
            timeAgo = "1 day ago",
            title = "Quayside Bike Rack C",
            body = "Temporary covered bike rack near the law courts, 20 spaces. Installed for summer — hope it stays!",
            likes = 210,
            comments = 34,
            tags = listOf(Tag.BIKE_RACK, Tag.TEMPORARY),
            position = LatLng(54.9684, -1.6099),
        ),
        Post(
            id = "Quayside Bike Rack D",
            username = "cycleNewcastle",
            timeAgo = "2 days ago",
            title = "Quayside Bike Rack D",
            body = "Bike rack near the Sage car park entrance, 16 spaces. Solid Sheffield stands.",
            likes = 95,
            comments = 7,
            tags = listOf(Tag.BIKE_RACK, Tag.APPROVED),
            position = LatLng(54.9690, -1.6102),
        ),
        Post(
            id = "Millennium Bridge Bike Rack",
            username = "quaysidecyclist",
            timeAgo = "4 hr. ago",
            title = "Millennium Bridge Bike Rack",
            body = "Bike rack at the north end of the Millennium Bridge, 10 spaces. Perfect before a riverside stroll.",
            likes = 176,
            comments = 22,
            tags = listOf(Tag.BIKE_RACK, Tag.APPROVED),
            hasImage = true,
            position = LatLng(54.9682, -1.6012),
        ),

        // ── Quayside EV Chargers ─────────────────────────────────────────
        Post(
            id = "Quayside EV Charger 1",
            username = "evDriver_NE",
            timeAgo = "2 hr. ago",
            title = "Fast EV Charger on the Quayside",
            body = "Fast EV charging point, 2 bays, 50 kW. Both bays free when I visited this morning — brilliant!",
            likes = 305,
            comments = 41,
            tags = listOf(Tag.ELECTRIC_CHARGER, Tag.APPROVED),
            hasImage = true,
            position = LatLng(54.9685, -1.6091),
        ),
        Post(
            id = "Quayside EV Charger 2",
            username = "greenMotorist",
            timeAgo = "6 hr. ago",
            title = "Proposed EV Charger — Quayside",
            body = "Proposed standard EV charging point, 4 bays, 22 kW. Awaiting planning permission — please support it!",
            likes = 198,
            comments = 53,
            tags = listOf(Tag.ELECTRIC_CHARGER, Tag.AWAITING_APPROVAL),
            position = LatLng(54.9683, -1.6096),
        ),
        Post(
            id = "Quayside EV Charger 3",
            username = "evDriver_NE",
            timeAgo = "1 day ago",
            title = "Rapid Charger near Malmaison",
            body = "Rapid EV charger near the Malmaison hotel, 2 bays, 150 kW. Fastest on the Quayside by far.",
            likes = 412,
            comments = 67,
            tags = listOf(Tag.ELECTRIC_CHARGER, Tag.APPROVED),
            hasImage = true,
            position = LatLng(54.9687, -1.6080),
        ),
        Post(
            id = "BALTIC Car Park EV",
            username = "balticArtsGoer",
            timeAgo = "3 days ago",
            title = "EV Charging at BALTIC Car Park",
            body = "EV charging in the BALTIC car park, 6 bays, 22 kW. Really handy when visiting the gallery.",
            likes = 231,
            comments = 19,
            tags = listOf(Tag.ELECTRIC_CHARGER, Tag.APPROVED),
            position = LatLng(54.9678, -1.6005),
        ),

        // ── Quayside Issues ──────────────────────────────────────────────
        Post(
            id = "Broken Cobblestones",
            username = "safetyFirst_NE",
            timeAgo = "1 hr. ago",
            title = "Broken Cobblestones — Trip Hazard!",
            body = "Several cobblestones loose near the Quayside walkway — a real trip hazard for pedestrians. Please fix ASAP.",
            likes = 876,
            comments = 134,
            tags = listOf(Tag.ISSUE),
            hasImage = true,
            position = LatLng(54.9689, -1.6075),
        ),
        Post(
            id = "Overflowing Bin",
            username = "tidyTyne",
            timeAgo = "2 hr. ago",
            title = "Overflowing Bin on the Quayside",
            body = "Public bin overflowing near the Quayside bar strip, reported multiple times. When will it be emptied?",
            likes = 541,
            comments = 88,
            tags = listOf(Tag.ISSUE),
            hasImage = true,
            position = LatLng(54.9686, -1.6082),
        ),

        // ── City Centre Issues ───────────────────────────────────────────
        Post(
            id = "Pothole on Grey Street",
            username = "cyclistNE1",
            timeAgo = "30 min. ago",
            title = "Dangerous Pothole on Grey Street",
            body = "Large pothole causing a hazard for cyclists near the Theatre Royal. I nearly came off my bike!",
            likes = 1030,
            comments = 576,
            tags = listOf(Tag.ISSUE),
            hasImage = true,
            position = LatLng(54.9748, -1.6140),
        ),
        Post(
            id = "Broken Street Light",
            username = "nightWalker_NCL",
            timeAgo = "4 hr. ago",
            title = "Broken Street Light — Grey Street",
            body = "Street light out on Grey Street junction. Temporary fix in place but the road feels unsafe after dark.",
            likes = 389,
            comments = 45,
            tags = listOf(Tag.ISSUE, Tag.TEMPORARY),
            position = LatLng(54.9751, -1.6135),
        ),
        Post(
            id = "Fly Tipping",
            username = "cleanNewcastle",
            timeAgo = "5 hr. ago",
            title = "Fly Tipping Behind Grainger Market",
            body = "Illegally dumped rubbish behind Grainger Market — bags of household waste left on the pavement.",
            likes = 720,
            comments = 102,
            tags = listOf(Tag.ISSUE),
            hasImage = true,
            position = LatLng(54.9745, -1.6148),
        ),
        Post(
            id = "Cracked Pavement",
            username = "accessibilityNow",
            timeAgo = "8 hr. ago",
            title = "Cracked Pavement Outside Grainger Market",
            body = "Cracked and uneven pavement slabs at the entrance. A real issue for wheelchair users and pushchairs.",
            likes = 634,
            comments = 79,
            tags = listOf(Tag.ISSUE, Tag.AWAITING_APPROVAL),
            position = LatLng(54.9742, -1.6161),
        ),
        Post(
            id = "Graffiti on Monument",
            username = "heritageNE",
            timeAgo = "1 day ago",
            title = "Graffiti on Grey's Monument",
            body = "Graffiti on the base of the Monument plinth. Really disrespectful. Cleaning is scheduled but when?",
            likes = 492,
            comments = 63,
            tags = listOf(Tag.ISSUE),
            hasImage = true,
            position = LatLng(54.9752, -1.6130),
        ),
        Post(
            id = "Blocked Drain",
            username = "floodWatch_NCL",
            timeAgo = "2 days ago",
            title = "Blocked Drain on Grey Street",
            body = "Storm drain blocked with debris causing pooling after rain. The whole pavement floods when it rains hard.",
            likes = 308,
            comments = 37,
            tags = listOf(Tag.ISSUE),
            position = LatLng(54.9746, -1.6143),
        ),

        // ── City Centre EV / Bike ────────────────────────────────────────
        Post(
            id = "Eldon Square EV Hub",
            username = "EVshopper",
            timeAgo = "6 hr. ago",
            title = "EV Hub at Eldon Square",
            body = "EV charging hub in the Eldon Square car park, 8 bays, 50 kW. Charge while you shop — love it.",
            likes = 267,
            comments = 28,
            tags = listOf(Tag.ELECTRIC_CHARGER, Tag.APPROVED),
            position = LatLng(54.9757, -1.6162),
        ),
        Post(
            id = "John Dobson St EV",
            username = "greenMotorist",
            timeAgo = "1 day ago",
            title = "Proposed EV Chargers — John Dobson St",
            body = "Proposed on-street EV chargers on John Dobson Street, 4 bays. Vote for it in the council survey!",
            likes = 183,
            comments = 31,
            tags = listOf(Tag.ELECTRIC_CHARGER, Tag.AWAITING_APPROVAL),
            position = LatLng(54.9754, -1.6145),
        ),
        Post(
            id = "Monument Bike Rack",
            username = "pedalNewcastle",
            timeAgo = "2 hr. ago",
            title = "Bike Rack Near Grey's Monument",
            body = "Sheffield stand bike rack near Grey's Monument, 20 spaces. Very central — use it every day.",
            likes = 158,
            comments = 14,
            tags = listOf(Tag.BIKE_RACK, Tag.APPROVED),
            position = LatLng(54.9753, -1.6133),
        ),
        Post(
            id = "Eldon Square Bike Rack",
            username = "cycleNewcastle",
            timeAgo = "3 hr. ago",
            title = "Covered Bike Rack at Eldon Square",
            body = "Covered bike rack outside Eldon Square's Percy Street entrance, 30 spaces. Stays dry even in the rain!",
            likes = 201,
            comments = 25,
            tags = listOf(Tag.BIKE_RACK, Tag.APPROVED),
            position = LatLng(54.9758, -1.6155),
        ),
        Post(
            id = "Grainger Street Bike Rack",
            username = "twoWheelsTyne",
            timeAgo = "5 hr. ago",
            title = "Proposed Bike Rack — Grainger Street",
            body = "Proposed new bike rack on Grainger Street. If approved there'll be space for 16 bikes. Please support it!",
            likes = 142,
            comments = 17,
            tags = listOf(Tag.BIKE_RACK, Tag.AWAITING_APPROVAL),
            position = LatLng(54.9740, -1.6158),
        ),

        // ── Leazes / St James' ───────────────────────────────────────────
        Post(
            id = "Broken Fence",
            username = "parkFriend",
            timeAgo = "1 day ago",
            title = "Broken Fence at Leazes Park",
            body = "Fence along the Leazes Park perimeter is broken — repair request submitted but no update yet.",
            likes = 198,
            comments = 22,
            tags = listOf(Tag.ISSUE, Tag.AWAITING_APPROVAL),
            position = LatLng(54.9791, -1.6220),
        ),
        Post(
            id = "Leazes Park",
            username = "parkLover",
            timeAgo = "2 hr. ago",
            title = "Such a Beautiful Park — Leazes!",
            body = "A beautiful Victorian park close to the city centre, perfect for a relaxing walk. Me and the kids went last night and it was the most fun we've had in ages!",
            likes = 400,
            comments = 239,
            tags = listOf(Tag.GREEN_SPACE, Tag.APPROVED),
            hasImage = true,
            position = LatLng(54.9789, -1.6218),
        ),
        Post(
            id = "Leazes Park Pond",
            username = "wildlifeNCL",
            timeAgo = "6 hr. ago",
            title = "Wildlife Conservation at Leazes Pond",
            body = "Proposed wildlife conservation area around the pond. Awaiting council approval — the ducks are waiting too!",
            likes = 312,
            comments = 47,
            tags = listOf(Tag.GREEN_SPACE, Tag.AWAITING_APPROVAL),
            hasImage = true,
            position = LatLng(54.9793, -1.6211),
        ),
        Post(
            id = "Leazes Park Bike Rack",
            username = "cycleNewcastle",
            timeAgo = "4 hr. ago",
            title = "Bike Rack at Leazes Park Entrance",
            body = "Bike rack at the main entrance to Leazes Park, 14 spaces. Great for cycling to the park.",
            likes = 89,
            comments = 6,
            tags = listOf(Tag.BIKE_RACK, Tag.APPROVED),
            position = LatLng(54.9787, -1.6224),
        ),
        Post(
            id = "Leazes Park EV Bay",
            username = "evDriver_NE",
            timeAgo = "3 hr. ago",
            title = "Temporary EV Bay near Leazes Park",
            body = "Temporary EV charging bay installed near Leazes Park for the football season. Handy on match days!",
            likes = 156,
            comments = 20,
            tags = listOf(Tag.ELECTRIC_CHARGER, Tag.TEMPORARY),
            position = LatLng(54.9785, -1.6228),
        ),
        Post(
            id = "New Cycle Lane",
            username = "saferStreetsNCL",
            timeAgo = "1 day ago",
            title = "New Protected Cycle Lane Approved!",
            body = "Approved protected cycle lane running alongside St James' Park. Great news for commuter cyclists.",
            likes = 523,
            comments = 88,
            tags = listOf(Tag.BIKE_RACK, Tag.APPROVED),
            position = LatLng(54.9756, -1.6218),
        ),
        Post(
            id = "St James' EV Bays",
            username = "greenMotorist",
            timeAgo = "5 hr. ago",
            title = "Proposed EV Bays at St James' Car Park",
            body = "Proposed EV charging bays in the St James' car park. Community consultation is ongoing — have your say!",
            likes = 247,
            comments = 39,
            tags = listOf(Tag.ELECTRIC_CHARGER, Tag.AWAITING_APPROVAL),
            position = LatLng(54.9758, -1.6214),
        ),
        Post(
            id = "St James' Park Litter",
            username = "cleanNewcastle",
            timeAgo = "2 hr. ago",
            title = "Litter Problem Near St James' on Match Days",
            body = "Persistent litter problem on the approach to St James' Park on match days. More bins needed urgently.",
            likes = 698,
            comments = 115,
            tags = listOf(Tag.ISSUE),
            hasImage = true,
            position = LatLng(54.9760, -1.6221),
        ),
        Post(
            id = "Gallowgate Green Space",
            username = "urbanGreenNCL",
            timeAgo = "8 hr. ago",
            title = "Proposed Pocket Park off Gallowgate",
            body = "Proposed pocket park on unused land off Gallowgate. Planning application submitted. Would be a lovely green corner!",
            likes = 374,
            comments = 58,
            tags = listOf(Tag.GREEN_SPACE, Tag.AWAITING_APPROVAL),
            position = LatLng(54.9762, -1.6210),
        ),

        // ── Central Station ──────────────────────────────────────────────
        Post(
            id = "Central Station EV Hub",
            username = "evDriver_NE",
            timeAgo = "1 hr. ago",
            title = "New EV Hub Outside Central Station",
            body = "Approved EV charging hub outside Central Station, 10 bays. Perfect for visitors arriving by train.",
            likes = 445,
            comments = 72,
            tags = listOf(Tag.ELECTRIC_CHARGER, Tag.APPROVED),
            hasImage = true,
            position = LatLng(54.9684, -1.6178),
        ),
        Post(
            id = "Central Station Bike Shelter",
            username = "cycleNewcastle",
            timeAgo = "3 hr. ago",
            title = "Proposed Covered Bike Shelter at Central Station",
            body = "Proposed covered bike shelter for 50 bikes outside the station. Linking rail and cycling at last!",
            likes = 389,
            comments = 61,
            tags = listOf(Tag.BIKE_RACK, Tag.AWAITING_APPROVAL),
            position = LatLng(54.9687, -1.6171),
        ),
        Post(
            id = "Station Road Roadworks",
            username = "commuterNCL",
            timeAgo = "30 min. ago",
            title = "Roadworks Outside Central Station",
            body = "Temporary road closure outside Central Station for utility works. Expected until end of month — allow extra time!",
            likes = 132,
            comments = 28,
            tags = listOf(Tag.ISSUE, Tag.TEMPORARY),
            position = LatLng(54.9682, -1.6183),
        ),
        Post(
            id = "Station Taxi Rank Issue",
            username = "accessibilityNow",
            timeAgo = "2 hr. ago",
            title = "Taxi Rank Blocking Pavement at Station",
            body = "Taxi rank blocking the pavement outside the station entrance. Serious accessibility concern for wheelchair users.",
            likes = 567,
            comments = 94,
            tags = listOf(Tag.ISSUE),
            position = LatLng(54.9680, -1.6175),
        ),
        Post(
            id = "Neville Street Bike Rack",
            username = "pedalNewcastle",
            timeAgo = "4 hr. ago",
            title = "Bike Rack on Neville Street",
            body = "Bike rack on Neville Street near the station exit, 18 spaces. Very handy for the morning commute.",
            likes = 112,
            comments = 8,
            tags = listOf(Tag.BIKE_RACK, Tag.APPROVED),
            position = LatLng(54.9683, -1.6168),
        ),

        // ── Ouseburn ─────────────────────────────────────────────────────
        Post(
            id = "Ouseburn Valley Green Space",
            username = "ouseburnTrust",
            timeAgo = "1 day ago",
            title = "Ouseburn Valley Green Space",
            body = "Community green space in the Ouseburn Valley, managed by the Ouseburn Trust. Volunteer days every Saturday!",
            likes = 487,
            comments = 76,
            tags = listOf(Tag.GREEN_SPACE, Tag.APPROVED),
            hasImage = true,
            position = LatLng(54.9712, -1.5981),
        ),
        Post(
            id = "Ouseburn Bike Rack",
            username = "gig_cyclist",
            timeAgo = "5 hr. ago",
            title = "Bike Rack at the Cluny",
            body = "Bike rack outside the Cluny music venue, 8 spaces. Cycle to the gig — no parking stress!",
            likes = 203,
            comments = 27,
            tags = listOf(Tag.BIKE_RACK, Tag.APPROVED),
            position = LatLng(54.9715, -1.5976),
        ),
        Post(
            id = "Ouseburn Fly Tipping",
            username = "cleanNewcastle",
            timeAgo = "3 hr. ago",
            title = "Fly Tipping in Ouseburn",
            body = "Dumped mattresses and furniture near the Ouseburn river path. Really spoils the area — please report it!",
            likes = 634,
            comments = 101,
            tags = listOf(Tag.ISSUE),
            hasImage = true,
            position = LatLng(54.9708, -1.5990),
        ),
        Post(
            id = "Ouseburn EV Charger",
            username = "evDriver_NE",
            timeAgo = "6 hr. ago",
            title = "Proposed EV Charger — Ouseburn",
            body = "Proposed EV charger for the Ouseburn creative quarter car park, 4 bays. Great idea for the area!",
            likes = 171,
            comments = 23,
            tags = listOf(Tag.ELECTRIC_CHARGER, Tag.AWAITING_APPROVAL),
            position = LatLng(54.9710, -1.5985),
        ),

        // ── Jesmond ──────────────────────────────────────────────────────
        Post(
            id = "Jesmond Dene Green Space",
            username = "deneDweller",
            timeAgo = "2 hr. ago",
            title = "Jesmond Dene — A Hidden Gem",
            body = "A beloved wooded dene running through Jesmond, maintained by the city council. The bluebells are out right now!",
            likes = 721,
            comments = 148,
            tags = listOf(Tag.GREEN_SPACE, Tag.APPROVED),
            hasImage = true,
            position = LatLng(54.9881, -1.5986),
        ),
        Post(
            id = "Jesmond Road EV Charger",
            username = "greenMotorist",
            timeAgo = "4 hr. ago",
            title = "On-Street EV Charger on Jesmond Road",
            body = "On-street EV charger on Jesmond Road, 2 bays, 7 kW. Perfect for overnight charging.",
            likes = 134,
            comments = 12,
            tags = listOf(Tag.ELECTRIC_CHARGER, Tag.APPROVED),
            position = LatLng(54.9842, -1.6041),
        ),
        Post(
            id = "Jesmond Metro Bike Rack",
            username = "metroAndBike",
            timeAgo = "1 hr. ago",
            title = "Covered Bike Rack at Jesmond Metro",
            body = "Covered bike rack at Jesmond Metro station, 24 spaces. Cycle and Metro combo — best commute in Newcastle.",
            likes = 298,
            comments = 33,
            tags = listOf(Tag.BIKE_RACK, Tag.APPROVED),
            position = LatLng(54.9839, -1.6038),
        ),
        Post(
            id = "Osborne Road Pothole",
            username = "cyclistNE1",
            timeAgo = "3 hr. ago",
            title = "Deep Pothole on Osborne Road",
            body = "Deep pothole on Osborne Road near the restaurant strip, reported by multiple residents. It's getting worse!",
            likes = 489,
            comments = 82,
            tags = listOf(Tag.ISSUE),
            hasImage = true,
            position = LatLng(54.9845, -1.6052),
        ),
    )


    // Derive visible posts from the single source of truth
    val visiblePosts by remember(activeFilters) {
        derivedStateOf {
            if (activeFilters.isEmpty()) posts
            else posts.filter { post ->
                activeFilters.all { it in post.tags }
            }
        }
    }

    // Per-post like state (keyed by post id)
    val likedPosts = remember { mutableStateMapOf<String, Boolean>() }
    val likeCounts = remember {
        mutableStateMapOf<String, Int>().also { map ->
            posts.forEach { map[it.id] = it.likes }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = padding)
            .padding(16.dp)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp),
        ) {
            items(Tag.entries) { tag ->
                val isSelected = tag in activeFilters
                FilterPill(
                    text = tag.displayName,
                    isSelected = isSelected,
                    onClick = {
                        activeFilters = if (isSelected) activeFilters - tag else activeFilters + tag
                    },
                    shadow = false,
                    icon = {
                        Icon(
                            imageVector = tag.icon,
                            contentDescription = null,
                            tint = if (isSelected) Color.White else Color(0xFF141414),
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    selectedColor = tag.color
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        if (visiblePosts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No posts match the selected filters.",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(modifier = Modifier.padding(top = 4.dp)) {
                itemsIndexed(visiblePosts) { index, post ->
                    PostCard(
                        post = post,
                        isLiked = likedPosts[post.id] == true,
                        likeCount = likeCounts[post.id] ?: post.likes,
                        onLike = {
                            val currently = likedPosts[post.id] == true
                            likedPosts[post.id] = !currently
                            likeCounts[post.id] = (likeCounts[post.id] ?: post.likes) + if (currently) -1 else 1
                        },
                        onClick = {
                            navController.navigate(Screen.DiscoverPost.createRoute(post.id))
                        }
                    )

                    if (index < visiblePosts.lastIndex) {
                        HorizontalDivider(
                            color = Color(0xFFCCDDCC),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun PostCard(
    post: Post,
    isLiked: Boolean,
    likeCount: Int,
    onLike: () -> Unit,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        // Header row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Avatar",
                modifier = Modifier.size(28.dp),
                tint = Color.Black
            )
            Text(
                text = post.username,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
            Text(
                text = "• ${post.timeAgo}",
                fontSize = 12.sp,
                color = Color.Gray
            )

            // Tag chips
            post.tags.forEach { tag ->
                Surface(
                    shape = RoundedCornerShape(50),
                    color = tag.color.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = tag.displayName,
                        fontSize = 10.sp,
                        color = tag.color,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Title
        Text(
            text = post.title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Body
        Text(
            text = post.body,
            fontSize = 13.sp,
            color = Color.DarkGray
        )

        // Image placeholder
        if (post.hasImage) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(width = 160.dp, height = 110.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF88BB88)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📷 Image", color = Color.White, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action row
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Like button — toggleable
            Row(
                modifier = Modifier.clickable(onClick = onLike),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.ThumbUp else Icons.Outlined.ThumbUp,
                    contentDescription = "Like",
                    modifier = Modifier.size(16.dp),
                    tint = if (isLiked) Color(0xFF388E3C) else Color.Gray
                )
                Text(
                    text = likeCount.toString(),
                    fontSize = 12.sp,
                    color = if (isLiked) Color(0xFF388E3C) else Color.Gray
                )
            }

            // Comments (navigates to post)
            Row(
                modifier = Modifier.clickable(onClick = onClick),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = "Comments",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Text(text = "${post.comments}", fontSize = 12.sp, color = Color.Gray)
            }

            // Share — Android system share sheet
            Row(
                modifier = Modifier.clickable {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, post.title)
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "${post.title}\n\n${post.body}\n\n— Shared via My Sustainable City"
                        )
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share post via…"))
                },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Text(text = "Share", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}


//@SuppressLint("ViewModelConstructorInComposable")
//@Composable
//@Preview
//fun DiscoverPreview() {
//    Text("Discover Screen Preview")
//}