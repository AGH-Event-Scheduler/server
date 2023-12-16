package pl.edu.agh.server.domain.exception

class CreatingEventOnArchivedOrganizationException(organizationId: Long) :
    RuntimeException("Organization $organizationId is archived and cannot add new events")
