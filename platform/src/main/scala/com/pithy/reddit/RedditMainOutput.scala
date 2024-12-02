package com.pithy.reddit

import com.pithy.PlatformMainOutput
import com.pithy.reddit.responses.DecodedGetCommentTreeFromPostBuilder

/**
 * Structured output of the Reddit api
 */
class RedditMainOutput(val output: DecodedGetCommentTreeFromPostBuilder)
    extends PlatformMainOutput {}
