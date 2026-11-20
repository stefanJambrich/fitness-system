import { useCallback, useEffect, useState } from 'react'
import { groupsApi } from '../../api/client'
import type { CreateGroupRequest, TrainerGroup } from '../../api/generated'
import { strings } from '../../common/strings'
import { isSessionError } from '../sessionErrors'

const byName = (a: TrainerGroup, b: TrainerGroup) => a.name.localeCompare(b.name)

export const useTrainerGroups = (onSessionExpired: () => void) => {
  const [groups, setGroups] = useState<TrainerGroup[]>()
  const [error, setError] = useState<string>()

  const handleSessionError = useCallback(async (e: unknown) => {
    if (await isSessionError(e)) {
      onSessionExpired()
      return true
    }
    return false
  }, [onSessionExpired])

  useEffect(() => {
    groupsApi.listTrainerGroups()
      .then(setGroups)
      .catch(async (e) => {
        if (!(await handleSessionError(e))) {
          setError(strings.errorGeneric)
        }
      })
  }, [handleSessionError])

  const createGroup = async (request: CreateGroupRequest) => {
    try {
      const created = await groupsApi.createGroup({ createGroupRequest: request })
      setGroups((current) => [...(current ?? []), created].sort(byName))
    } catch (e) {
      await handleSessionError(e)
      throw e
    }
  }

  return { groups, error, createGroup }
}