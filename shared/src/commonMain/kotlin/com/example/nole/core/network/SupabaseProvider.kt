package com.example.nole.core.network

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

class SupabaseProvider {
    val URL = "https://hdicbxtrvbzqspobeeuo.supabase.co"
    val ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImhkaWNieHRydmJ6cXNwb2JlZXVvIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTQ4MjcwMjEsImV4cCI6MjExMDQwMzAyMX0.T6UMO0BmhIvMwNJ9hanubfxCRe5gFwmgcZvGddKCCxQ"

    val client = createSupabaseClient(
        supabaseUrl = URL.trim(),
        supabaseKey = ANON_KEY.trim(),
    ) {
        install(Postgrest)
        install(Realtime)
    }
}
