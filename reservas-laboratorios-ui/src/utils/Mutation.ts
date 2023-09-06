import { useState } from "react";
import { QueryResult } from "./Query";

type Method = "GET" | "POST" | "PUT" | "DELETE" | "PATCH";

type Mutation<T, V> = [executeMutation: (variables: V) => Promise<QueryResult<T>>, queryResult: QueryResult<T>];
const BASE_URL = import.meta.env.VITE_API_URL + ":" + import.meta.env.VITE_API_PORT + "/";

export function useCustomMutation<T, V>(path: string, method?: Method): Mutation<T, V> {
  const [data, setData] = useState<T>();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string>();

  const executeMutation: (variables: V) => Promise<QueryResult<T>> = async (variables) => {
    setLoading(true);

    // console.log("Mutating data from " + BASE_URL + path + " with variables " + JSON.stringify(variables));

    return fetch(BASE_URL + path, {
      method: method || "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(variables),
    })
      .then(async (response: Response) => {
        if (response.ok) {
          const responseData = (await response.json()).data as T;
          setData(responseData);
          if (error || error?.length === 0) {
            setError(undefined);
          }
          return { data: responseData, loading: false };
        }
        throw response;
      })
      .catch(async (response: TypeError | Response) => {
        if (response instanceof TypeError) {
          console.error(`Error executing mutation. Message: ${response.message}. Stack: ${response.stack}`);
          setError(response.message);
          return { loading: false, error: response.message };
        }
        if (response instanceof Response) {
          let errorMessage = await response.json();
          errorMessage = errorMessage.error || errorMessage.message || errorMessage;

          console.error(`Error executing mutation. Status: ${response.status}. Response: ${errorMessage}`);
          setError(errorMessage);
          return { loading: false, error: errorMessage };
        }

        setError("An unkown error ocurred");
        return { loading: false, error: "An unkown error ocurred" };
      })
      .finally(() => {
        setLoading(false);
      });
  };

  return [executeMutation, { data, loading, error }];
}
