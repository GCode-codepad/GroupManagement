import React, { useState, useEffect, useRef } from 'react';

function App() {
  const [roomId, setRoomId] = useState('');
  const [submittedRoomId, setSubmittedRoomId] = useState(null);
  const [messages, setMessages] = useState([]);
  const eventSourceRef = useRef(null);

  useEffect(() => {
    if (!submittedRoomId) return;

    const eventSource = new EventSource(`http://localhost:3000/events?room=${submittedRoomId}`);
    eventSourceRef.current = eventSource;

    eventSource.onopen = (e) => {
      console.log('SSE connection opened:', e);
    };

    eventSource.onmessage = (event) => {
      console.log('Raw SSE message:', event.data);
      try {
        const data = JSON.parse(event.data);
        console.log('Parsed SSE message:', data);
        setMessages((prevMessages) => [...prevMessages, data]);
      } catch (err) {
        console.error('Error parsing event data:', err);
      }
    };

    eventSource.onerror = (err) => {
      console.error('EventSource encountered an error:', err);
      eventSource.close();
    };

    return () => {
      eventSource.close();
    };
  }, [submittedRoomId]);

  const handleSubmit = (e) => {
    e.preventDefault();
    setMessages([]); // Clear previous messages
    if (eventSourceRef.current) {
      eventSourceRef.current.close();
    }
    setSubmittedRoomId(roomId);
  };

  return (
    <div style={{ padding: '1rem' }}>
      <h1>React SSE Client</h1>
      <form onSubmit={handleSubmit}>
        <label>
          Enter Room ID:
          <input
            type="text"
            value={roomId}
            onChange={(e) => setRoomId(e.target.value)}
            required
          />
        </label>
        <button type="submit">Connect</button>
      </form>

      {submittedRoomId && (
        <h2>Listening to room: {submittedRoomId}</h2>
      )}

      <ul>
        {messages.map((msg, index) => (
          <li key={index}>
            <pre>{JSON.stringify(msg, null, 2)}</pre>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default App;
