import { useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom'
import { checkAuth } from '../api'

export default function ProtectedRoute({ children }) {
  const [status, setStatus] = useState('checking') // 'checking' | 'ok' | 'denied'

  useEffect(() => {
    checkAuth()
      .then(authenticated => setStatus(authenticated ? 'ok' : 'denied'))
      .catch(() => setStatus('denied'))
  }, [])

  if (status === 'checking') return null
  if (status === 'denied') return <Navigate to="/login" replace />
  return children
}
