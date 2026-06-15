export async function checkAuth() {
  const res = await fetch('/api/auth/status', { credentials: 'include' })
  const data = await res.json()
  return data.authenticated === true
}

export async function login(email, password) {
  const body = new URLSearchParams({ email, password })
  const res = await fetch('/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: body.toString(),
    credentials: 'include',
  })
  const data = await res.json()
  return data
}

export async function logout() {
  await fetch('/logout', { method: 'POST', credentials: 'include' })
}

export async function getWeather(city) {
  const res = await fetch(`/weather?city=${encodeURIComponent(city)}`, {
    credentials: 'include',
  })
  return res.json()
}

export async function getUsers() {
  const res = await fetch('/api/users', { credentials: 'include' })
  return res.json()
}

export async function saveUser(name, city, address) {
  const res = await fetch('/api/save-user', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    credentials: 'include',
    body: JSON.stringify({ name, city, address }),
  })
  return res.json()
}

export async function deleteUser(id) {
  const res = await fetch(`/api/user/${encodeURIComponent(id)}`, {
    method: 'DELETE',
    credentials: 'include',
  })
  return res.json()
}
