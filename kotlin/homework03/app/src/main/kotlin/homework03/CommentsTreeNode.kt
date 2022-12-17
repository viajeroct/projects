package homework03

data class CommentsTreeNode(
    val comment: CommentMessage? = null,
    val children: List<CommentsTreeNode?> = emptyList()
) {
    override fun toString(): String {
        val s = children.map { it?.comment?.uniqueId }.joinToString(separator = ",", prefix = "[", postfix = "]")
        return "CommentsTreeNode(comment=$comment, children=$s)"
    }
}
