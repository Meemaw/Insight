import React, { useEffect, useState } from 'react';
import ky from 'ky-universal';

type Comment = {
  postId: number;
  id: number;
  name: string;
  email: string;
  body: string;
};

const TestcafeBug = () => {
  const [comments, setComments] = useState<Comment[]>([]);

  useEffect(() => {
    ky.get('https://jsonplaceholder.typicode.com/comments')
      .json<Comment[]>()
      .then(setComments);
  });

  return (
    <ul>
      {comments.map(comm => (
        <li key={comm.id}>{comm.body}</li>
      ))}
    </ul>
  );
};

export default TestcafeBug;
