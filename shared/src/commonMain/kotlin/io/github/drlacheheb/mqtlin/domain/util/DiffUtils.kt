package io.github.drlacheheb.mqtlin.domain.util

enum class DiffType {
    UNCHANGED,
    ADDED,
    DELETED,
}

data class DiffLine(
    val type: DiffType,
    val text: String,
    val oldLineNumber: Int? = null,
    val newLineNumber: Int? = null,
)

data class DiffResult(
    val lines: List<DiffLine>,
    val additions: Int,
    val deletions: Int,
    val hasChanges: Boolean,
)

object DiffUtils {
    /**
     * Computes a line-by-line diff between [oldText] and [newText].
     * If [prettifyJson] is true and payloads are valid JSON, payloads are formatted first
     * for clean key-by-key visual structural comparison.
     */
    fun computeDiff(
        oldText: String,
        newText: String,
        prettifyJson: Boolean = true,
    ): DiffResult {
        val effectiveOld = formatIfJson(oldText, prettifyJson)
        val effectiveNew = formatIfJson(newText, prettifyJson)

        val oldLines = if (effectiveOld.isEmpty()) emptyList() else effectiveOld.lines()
        val newLines = if (effectiveNew.isEmpty()) emptyList() else effectiveNew.lines()

        val lcsMatrix = computeLcsMatrix(oldLines, newLines)
        return backtrackDiff(oldLines, newLines, lcsMatrix)
    }

    private fun formatIfJson(
        text: String,
        prettifyJson: Boolean,
    ): String =
        if (prettifyJson && JsonUtils.isValidJson(text)) {
            JsonUtils.formatOrRaw(text)
        } else {
            text
        }

    private fun backtrackDiff(
        oldLines: List<String>,
        newLines: List<String>,
        lcsMatrix: Array<IntArray>,
    ): DiffResult {
        var i = oldLines.size
        var j = newLines.size
        var additions = 0
        var deletions = 0
        val reversedDiff = mutableListOf<DiffLine>()

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && oldLines[i - 1] == newLines[j - 1]) {
                reversedDiff.add(DiffLine(DiffType.UNCHANGED, oldLines[i - 1], i, j))
                i--
                j--
            } else if (j > 0 && (i == 0 || lcsMatrix[i][j - 1] >= lcsMatrix[i - 1][j])) {
                reversedDiff.add(DiffLine(DiffType.ADDED, newLines[j - 1], newLineNumber = j))
                additions++
                j--
            } else if (i > 0) {
                reversedDiff.add(DiffLine(DiffType.DELETED, oldLines[i - 1], oldLineNumber = i))
                deletions++
                i--
            }
        }

        return DiffResult(
            lines = reversedDiff.reversed(),
            additions = additions,
            deletions = deletions,
            hasChanges = additions > 0 || deletions > 0,
        )
    }

    private fun computeLcsMatrix(
        a: List<String>,
        b: List<String>,
    ): Array<IntArray> {
        val n = a.size
        val m = b.size
        val dp = Array(n + 1) { IntArray(m + 1) }

        for (i in 1..n) {
            for (j in 1..m) {
                if (a[i - 1] == b[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1] + 1
                } else {
                    dp[i][j] = maxOf(dp[i - 1][j], dp[i][j - 1])
                }
            }
        }
        return dp
    }
}
