<!DOCTYPE html>
<html>
<head>
    <title>Overall Stats</title>
    <#include "commonIncludes.ftl">
</head>
<body>
    <#include "adminLinks.ftl">
    <h3>Today Stats</h3>
    <#assign stats = todayStats>
    <#include "stats.ftl">
    <h3>Yesterday Stats</h3>
    <#assign stats = yesterdayStats>
    <#include "stats.ftl">
    <h3>Week Stats</h3>
    <#assign stats = weekStats>
    <#include "stats.ftl">
    <h3>Month Stats</h3>
    <#assign stats = monthStats>
    <#include "stats.ftl">
    <h3>Overall Stats</h3>
    <#assign stats = overallStats>
    <#include "stats.ftl">
</body>
</html>
